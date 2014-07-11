package com.minutekey

trait KeyMonitorService {
  var brassKeyCnt: Int = _
  def ticketGenerator: TicketGenerator
  def checkKeyStatus: Unit = {
    if (brassKeyCnt < 20)
      ticketGenerator.create("Brass keys low")
  }
  def brassKeyCount(cnt: Int) = { brassKeyCnt = cnt }

}

class DefaultKeyMonitorService(val ticketGenerator: TicketGenerator) extends KeyMonitorService {


}

