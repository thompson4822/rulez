package com.minutekey

trait ClickMonitorService {
  var cancelClickCnt: Int = _
  def ticketGenerator: TicketGenerator
  def checkCancelClicks: Unit = {
    if (cancelClickCnt > 2)
      ticketGenerator.create("Cash Payment - excessive cancels")
  }
  def cancelClickCount(cnt: Int) = { cancelClickCnt = cnt }

}

class DefaultClickMonitorService(val ticketGenerator: TicketGenerator) extends ClickMonitorService {


}

