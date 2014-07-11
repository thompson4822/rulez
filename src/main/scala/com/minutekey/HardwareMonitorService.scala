package com.minutekey

trait HardwareMonitorService {
  def ticketGenerator: TicketGenerator
  def checkHardwareStatus: Unit = ???

}

class DefaultHardwareMonitorService(val ticketGenerator: TicketGenerator) extends HardwareMonitorService {

  
}

