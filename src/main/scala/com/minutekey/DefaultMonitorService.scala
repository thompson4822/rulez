package com.minutekey

import java.sql.Timestamp
import com.minutekey.model._
import com.minutekey.Configuration._
import java.util.{Date, Calendar, Timer, TimerTask}
import org.slf4j.LoggerFactory
import scala.collection.mutable.ListBuffer

class DefaultMonitorService(ticketGenerator: TicketGenerator) extends MonitorService {
  def logger = LoggerFactory.getLogger("default")

  val logRecords: ListBuffer[LogRecord] = new ListBuffer[LogRecord]

  var currentScreen: ScreenRecord = _
  var timer: Timer = new Timer()

  def pack[A](ls: List[A]): List[List[A]] = {
    if (ls.isEmpty) List(List())
    else {
      val (packed, next) = ls span { _ == ls.head }
      if (next == Nil) List(packed)
      else packed :: pack(next)
    }
  }

  def recordsDuring(startDate: Timestamp, endDate: Option[Timestamp] = None): Seq[LogRecord] = {
    ???
  }

  def purchasesToday: Int = {
    ???
  }

  def purchaseCount: Int = {
    logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry.getTime <= getNoonToday.getTime)
      .collect{case record: ScreenRecord => record}
      .count(r => r.name == "Remove Key" && r.attributes("reason") == "transaction successful")
  }

  def purchaseWindowHours: Int = 0

  def cancelClicksExceeded: Option[String] = {
    val screenCancels: List[String] = logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry.getTime >= timeSinceLastCheck)
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
    val brassKeysLowCount = logRecords.filter(record => isToday(record.timeOfEntry) && record.timeOfEntry.getTime >= timeSinceLastCheck)
      .collect{ case record: KeyEjectRecord => record }
      .filter(r => r.SKU.contains("BRAS"))
      .count(r => r.quantity < Configuration.brassLowAmount)

    if (brassKeysLowCount > 0) {
      ticketGenerator.create(s"Brass keys low.  Less than $brassLowAmount remain of one or more SKU.")
    }

  }

  // When was the last time we checked for purchases after noon today
  var lastCheckForPurchases: Timestamp = _


  def now: Calendar = Calendar.getInstance()

  def isToday(time: Timestamp): Boolean = {
    val oldTime = Calendar.getInstance()
    oldTime.setTimeInMillis(time.getTime)
    oldTime.get(Calendar.DATE) == now.get(Calendar.DATE)
  }

  // TODO - This will produce multiple tickets if there are no purchases after noon and the machine is rebooted
  override def checkForPurchases(): Unit = {
    // Need to set a flag to not check for purchases again today
    if ((lastCheckForPurchases == null || !isToday(lastCheckForPurchases)) && purchaseCount == 0) {
      ticketGenerator.create("No purchases made today.")
    }
    lastCheckForPurchases = new Timestamp(now.getTimeInMillis)
  }

  override def add(records: Seq[LogRecord]): Unit = { 
    logRecords ++= records
    checkKiosk
  }

  def getNoonToday: Date = {
    val day = new Date()
    val cal = Calendar.getInstance()
    cal.setTime(day)
    cal.set(Calendar.HOUR_OF_DAY, 12)
    cal.set(Calendar.MINUTE,      cal.getMinimum(Calendar.MINUTE))
    cal.set(Calendar.SECOND,      cal.getMinimum(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND))
    cal.getTime
  }

  def afterNoon: Boolean = {
    Calendar.getInstance().getTimeInMillis > getNoonToday.getTime
  }

  var lastCheckTime: Timestamp = _

  def minutes(count: Int): Long = count * 60 * 1000

  def timeSinceLastCheck: Long = {
    if(lastCheckTime == null)
      lastCheckTime = new Timestamp(0L)
    Calendar.getInstance().getTimeInMillis - lastCheckTime.getTime
  }

  override def checkKiosk: Unit = {
    if(lastCheckTime == null || timeSinceLastCheck > minutes(30)) {
      checkCancelClicks()
      brassKeysLow()
      if(afterNoon)
        checkForPurchases()
      lastCheckTime = new Timestamp(Calendar.getInstance().getTimeInMillis)

    }
  }
}

//helper class
class ScreenTimeoutTask(currentScreen: ScreenRecord, ticketGenerator: TicketGenerator) extends TimerTask {
  override def run(): Unit = {
    ticketGenerator.create(currentScreen.name)
  }
}