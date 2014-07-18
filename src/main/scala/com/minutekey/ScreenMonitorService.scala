package com.minutekey

import java.sql.Timestamp
import java.util.{TimerTask, Timer}

import model._


/**
 * Created by steve on 7/10/14.
 */

/*
I think we can just track the current screen using the page entry event from within Default Monitor Service

That way we don't have to dink around with timers.
*/

/*
trait ScreenMonitorService {
  def currentScreen: ScreenRecord
  def ticketGenerator: TicketGenerator
}

//class ScreenTimeoutTask(currentScreen: ScreenRecord, ticketGenerator: TicketGenerator) extends TimerTask {
//  override def run(): Unit = {
//    ticketGenerator.create(currentScreen.name)
//  }
//}

class DefaultScreenMonitorService(val currentScreen: ScreenRecord, val ticketGenerator: TicketGenerator) extends ScreenMonitorService {
  import DefaultScreenMonitorService._

  timer.cancel()
  timer = new Timer()
  timer.schedule(new ScreenTimeoutTask(currentScreen, ticketGenerator), currentScreen.timeoutSeconds*1000)
}

object DefaultScreenMonitorService {
  var timer: Timer = new Timer()
}
*/
