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
  /*
  Things we need:
  - collection of click records

  Things to do:
  - by screen, monitor multiple cancels in a session

  - if over multiple subsequent sessions customers are canceling

   */

}

