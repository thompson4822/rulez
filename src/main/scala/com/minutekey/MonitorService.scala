package com.minutekey

import com.minutekey.model.ScreenRecord
import java.util.{Timer, TimerTask}

trait MonitorService {
  var brassKeyCnt: Int = _
  var disconnectCnt: Int = _
  var purchaseCnt: Int = _
  var purchaseWindowHrs: Int = _
  var cancelClickCnt: Int = _
  var currentScreen: ScreenRecord = _
  var timer: Timer = new Timer()

  def ticketGenerator: TicketGenerator

  def purchaseCount(cnt: Int) = { purchaseCnt = cnt }
  def purchaseWindowHours(hours: Int) = { purchaseWindowHrs = hours }
  def cancelClickCount(cnt: Int) = { cancelClickCnt = cnt }
  def disconnectCount(cnt: Int) = { disconnectCnt = cnt }
  def brassKeyCount(cnt: Int) = { brassKeyCnt = cnt }

  def checkHardwareStatus: Unit = {
    if (disconnectCnt > 1)
      ticketGenerator.create("Bill Collector")
  }

  def checkCancelClicks: Unit = {
    if (cancelClickCnt > 2)
      ticketGenerator.create("Cash Payment - excessive cancels")
  }

  def checkKeyStatus: Unit = {
    if (brassKeyCnt < 20)
      ticketGenerator.create("Brass keys low")
  }

  def checkForPurchases: Unit = {
    if (purchaseCnt == 0)
      ticketGenerator.create("No purchases made today.")
  }

  def scheduleTimer: Unit = {
    timer.cancel()
    timer = new Timer()
    timer.schedule(new ScreenTimeoutTask(currentScreen, ticketGenerator), currentScreen.timeoutSeconds*1000)
  }
}

class DefaultMonitorService(val ticketGenerator: TicketGenerator) extends MonitorService {
  /*
  Things we need:
  - collection of click records

  Things to do:
  - by screen, monitor multiple cancels in a session

  - if over multiple subsequent sessions customers are canceling

   */

}

object DefaultMonitorService {
  var timer: Timer = new Timer()
}

//helper class
class ScreenTimeoutTask(currentScreen: ScreenRecord, ticketGenerator: TicketGenerator) extends TimerTask {
  override def run(): Unit = {
    ticketGenerator.create(currentScreen.name)
  }
}