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

  def pack[A](ls: List[A]): List[List[A]] = {
    if (ls.isEmpty) List(List())
    else {
      val (packed, next) = ls span { _ == ls.head }
      if (next == Nil) List(packed)
      else packed :: pack(next)
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

  def cancelClicksExceeded: Option[String] = {
    val screenCancels: List[String] = logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry >= lastCheckTime)
      .collect { case record: ButtonClickRecord => record }
      .filter(_.button == "Cancel")
      .map(_.screen)
      .toList
    // If there is more than one cancel in a row for any screen, return the name of the offending screen
    pack(screenCancels).find(list => list.length > 1).map(_.head)
  }

  override def checkCancelClicks(): Unit = {
    cancelClicksExceeded.map(screen => ticketGenerator.create(s"Excessive cancels on screen ${screen}"))
  }

  override def checkHardwareStatus(): Unit = {
    val billAcceptorDisconnects = logRecords.filter(record => isToday(record.timeOfEntry))
      .collect{ case record: BillAcceptorDisconnectedRecord => record }
      .count(r => r.description == "Acceptor disconnected")

      if (billAcceptorDisconnects > Configuration.billAcceptorDisconnectLimit) {
        ticketGenerator.create(s"Bill Collector disconnected $billAcceptorDisconnects times today")
      }
  }

  override def brassKeysLow(): Unit = {
    val brassKeysLowCount = logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry >= lastCheckTime)
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
  override def checkForPurchases(): Unit = {
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

  def checkUnidentifiedKeys(): Unit = {
    val incidenceCount = logRecords.filter(record => isToday(record.timeOfEntry))
      .collect{ case record: InvalidKeyTypeRecord => record }
      .length
    if (incidenceCount > 5) {
      ticketGenerator.create(s"Suspicious number of unidentified keys encountered, could there be a hardware configuration issue?")
    }

  }

  override def checkKiosk: Unit = {
    if(lastCheckTime == null || timeSinceLastCheck > minutes(Configuration.monitorFrequency)) {
      checkCancelClicks()
      brassKeysLow()
      if(afterNoon)
        checkForPurchases()
      checkBillAcceptorConnects()
      checkBillAcceptorCassetteRemovals()
      checkUnidentifiedKeys()
      lastCheckTime = DateTime.now

    }
  }

  override def checkBillAcceptorConnects(): Unit = {
    val incidenceCount = logRecords.filter(record => isToday(record.timeOfEntry))
      .collect{ case record: BillAcceptorConnectedRecord => record }
      .count(r => r.description == "Acceptor connected")
    if (incidenceCount > 0) {
      ticketGenerator.create(s"Suspicious Bill Acceptor Connectivity")
    }
  }

  override def checkBillAcceptorCassetteRemovals(): Unit = {
    val incidenceCount = logRecords.filter(record => isToday(record.timeOfEntry))
      .collect{ case record: BillAcceptorCassetteRemovedRecord => record }
      .count(r => r.description == "Cassette is Removed")
    if (incidenceCount > 0) {
      ticketGenerator.create(s"Suspicious Bill Acceptor Cassette Handling")
    }
  }

}
