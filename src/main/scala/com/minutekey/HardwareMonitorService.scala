package com.minutekey

trait HardwareMonitorService {
  var disconnectCnt: Int = _
  def ticketGenerator: TicketGenerator
  def checkHardwareStatus: Unit = {
    if (disconnectCnt > 1)
      ticketGenerator.create("Bill Collector")
  }
  def disconnectCount(cnt: Int) = { disconnectCnt = cnt }

}

class DefaultHardwareMonitorService(val ticketGenerator: TicketGenerator) extends HardwareMonitorService {


}

