package com.minutekey

import com.minutekey.model.LogRecord

/**
 * Created by steve on 7/16/14.
 */
trait MonitorService {
  def add(records: Seq[LogRecord])

  def ticketGenerator: TicketGenerator

  def checkKiosk(): Unit

  def checkHardwareStatus(): Unit

  def checkCancelClicks(): Unit

  def checkKeyStatus(): Unit

  def checkForPurchases(): Unit

  def brassKeyCount: Int

  def disconnectCount: Int

  def purchaseCount: Int

  def purchaseWindowHours: Int

  def cancelClicksExceeded: Option[String]

}
