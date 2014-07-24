package com.minutekey.monitor

import com.github.nscala_time.time.Imports._
import com.minutekey.Configuration._
import com.minutekey.model._
import com.minutekey.{Configuration, TicketGenerator}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
 * Created by steve on 7/18/14.
 */
class DefaultMonitorService(ticketGenerator: TicketGenerator) extends MonitorService {
  def logger = LoggerFactory.getLogger("default")

  val logRecords: ListBuffer[LogRecord] = new ListBuffer[LogRecord]

  var currentScreen: ScreenRecord = _

  def pack[A](ls: List[A], equalityFun: (A, A) => Boolean): List[List[A]] = {
    if (ls.isEmpty) List(List())
    else {
      val (packed, next) = ls span { equalityFun(_, ls.head) }
      if (next == Nil) List(packed)
      else packed :: pack(next, equalityFun)
    }
  }

  def recordsDuring(startDate: DateTime, endDate: Option[DateTime] = None): Seq[LogRecord] = {
    ???
  }

  def purchasesToday: Int = {
    ???
  }

  def purchaseCount: Int = {
    logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry <= getNoonToday)
      .collect{case record: ScreenRecord => record}
      .count(r => r.screen == "Remove Key" && r.attributes("reason") == "transaction successful")
  }

  def purchaseWindowHours: Int = 0

  override def checkCancelClicks(records: List[LogRecord]): Unit = {
    val screenCancels: List[String] = records.filter(_.timeOfEntry >= lastCheckTime)
      .collect { case record: ButtonClickRecord => record }
      .filter(_.button == "Cancel")
      .map(_.screen)
      .toList
    // If there is more than one cancel in a row for any screen, return the name of the offending screen
    val screenWithMultipleCancels = pack(screenCancels, (x: String, y: String) => x == y)
      .find(list => list.length > 1)
      .map(_.head)
    screenWithMultipleCancels.map(screen => ticketGenerator.create(s"Excessive cancels on screen ${screen}"))
  }

  override def checkHardwareStatus(records: List[LogRecord]): Unit = {
    val billAcceptorDisconnects = records
      .collect{ case record: BillAcceptorDisconnectedRecord => record }
      .count(r => r.description == "Acceptor disconnected")

      if (billAcceptorDisconnects > Configuration.billAcceptorDisconnectLimit) {
        ticketGenerator.create(s"Bill Collector disconnected $billAcceptorDisconnects times today")
      }
  }

  override def brassKeysLow(records: List[LogRecord]): Unit = {
    val brassKeysLowCount = records.filter(_.timeOfEntry >= lastCheckTime)
      .collect{ case record: KeyEjectRecord => record }
      .filter(r => r.SKU.contains("BRAS"))
      .count(r => r.quantity < Configuration.brassLowAmount)

    if (brassKeysLowCount > 0) {
      ticketGenerator.create(s"Brass keys low.  Less than $brassLowAmount remain of one or more SKU.")
    }

  }

  // When was the last time we checked for purchases after noon today
  var lastCheckForPurchases: DateTime = _


  def isToday(dateTime: DateTime): Boolean = dateTime.dayOfYear() == DateTime.now.dayOfYear()

  // TODO - This will produce multiple tickets if there are no purchases after noon and the machine is rebooted
  override def checkForPurchases(records: List[LogRecord]): Unit = {
    // Need to set a flag to not check for purchases again today
    if ((lastCheckForPurchases == null || !isToday(lastCheckForPurchases)) && purchaseCount == 0) {
      ticketGenerator.create("No purchases made today.")
    }
    lastCheckForPurchases = DateTime.now
  }

  override def add(records: Seq[LogRecord]): Unit = {
    logRecords ++= records
    checkKiosk
  }

  def getNoonToday: DateTime = 
    DateTime.now.withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0)

  def afterNoon: Boolean = DateTime.now > getNoonToday

  var _lastCheckTime: DateTime = _

  def lastCheckTime = {
    if(_lastCheckTime == null)
      _lastCheckTime = DateTime.yesterday
    _lastCheckTime
  }

  def lastCheckTime_=(dateTime: DateTime) = _lastCheckTime = dateTime

  def minutes(count: Int): Long = count * 60 * 1000

  def timeSinceLastCheck: Long = {
    if(lastCheckTime == null)
      lastCheckTime = DateTime.yesterday
    (lastCheckTime to DateTime.now).millis
  }

  def recordsToday: List[LogRecord] = logRecords.filter(record => isToday(record.timeOfEntry)).toList

  def checkUnidentifiedKeys(records: List[LogRecord]): Unit = {
    val incidenceCount = records
      .collect{ case record: InvalidKeyTypeRecord => record }
      .length
    if (incidenceCount > 5) {
      ticketGenerator.create(s"Suspicious number of unidentified keys encountered, could there be a hardware configuration issue?")
    }

  }

  override def checkKiosk: Unit = {
    if(lastCheckTime == null || timeSinceLastCheck > minutes(Configuration.monitorFrequency)) {
      val records = recordsToday
      checkCancelClicks(records)
      brassKeysLow(records)
      if(afterNoon)
        checkForPurchases(records)
      checkBillAcceptorConnects(records)
      checkBillAcceptorCassetteRemovals(records)
      checkUnidentifiedKeys(records)
      checkSessionConclusion(records)
      checkScreenTransition(records)
      checkKioskNotWorking(records)
      lastCheckTime = DateTime.now
    }
  }

  override def checkBillAcceptorConnects(records: List[LogRecord]): Unit = {
    val incidenceCount = records
      .collect{ case record: BillAcceptorConnectedRecord => record }
      .count(r => r.description == "Acceptor connected")
    if (incidenceCount > 0) {
      ticketGenerator.create(s"Suspicious Bill Acceptor Connectivity")
    }
  }

  override def checkBillAcceptorCassetteRemovals(records: List[LogRecord]): Unit = {
    val incidenceCount = records
      .collect{ case record: BillAcceptorCassetteRemovedRecord => record }
      .count(r => r.description == "Cassette is Removed")
    if (incidenceCount > 0) {
      ticketGenerator.create(s"Suspicious Bill Acceptor Cassette Handling")
    }
  }

  // If the last session did not conclude on the remove key screen, generate a ticket
  override def checkSessionConclusion(records: List[LogRecord]): Unit = {
    val customerScreenRecords = records
      .collect{ case record: ScreenRecord => record}
      .filter(_.sessionId.isDefined)
    val sessions: List[List[ScreenRecord]] = pack(customerScreenRecords.toList, (x: ScreenRecord, y: ScreenRecord) => x.sessionId == y.sessionId)
    if(sessions.length > 1) {
      val concludedWithRemove = sessions.takeRight(2).head
        .last.screen == "Remove Key"
      if(!concludedWithRemove)
        ticketGenerator.create("Customer Session Did Not Exit In The Expected Manner")
    }
  }

  // Hard rule - if we've been on this screen more than 12 minutes, something is definitely wrong!
  def timeoutExceeded(screen: ScreenRecord): Boolean = {
    screen.screen != "Attract Loop" && (DateTime.now - 12.minutes) > screen.timeOfEntry
  }

  // If the last screen hasn't seen a transition in the required amount of time and is not the attract loop, generate a ticket
  override def checkScreenTransition(records: List[LogRecord]): Unit = {
    val lastScreenEntry: Option[ScreenRecord] = records
      .collect{ case record: ScreenRecord => record }
      .lastOption
    lastScreenEntry.map(screen => if(timeoutExceeded(screen)) ticketGenerator.create(s"Customer Screen '${screen.screen}' Did Not Transition Within Its Timeout Period"))
  }

  // If subsequent sessions have been canceled because the kiosk was not working, we need to generate a ticket
  override def checkKioskNotWorking(records: List[LogRecord]): Unit = {
    // Get sessions today ...
    val sessions = records
      .collect{ case record: ScreenRecord => record }
      .flatMap(_.sessionId)
      .distinct
      .toList

    // For each session, try to find a survey response record that was Kiosk Not Working
    //sessions.
    // If subsequent responses of this nature, generate ticket
    val surveySessions = records
      .collect{ case survey: SurveyResponseRecord => survey }
      .filter(survey => survey.response == "KioskNotWorking")
      .map(survey => survey.sessionId)

    if(sessions != Nil && surveySessions != Nil) {
      // Produces sequences of items over time based on whether they had "KioskNotWorking" survey info. If any such sequence is > 1 (there were more than one subsequent
      // session with this outcome) then generate a ticket.
      pack(sessions.map(session => if (surveySessions.contains(session)) 'hasSurvey else 'noSurvey), (x: Symbol, y: Symbol) => x == y)
        .find(series => series.head == 'hasSurvey && series.length > 1)
        .map(_ => ticketGenerator.create("Surveys Indicating Kiosk Not Working"))
    }

  }
}
