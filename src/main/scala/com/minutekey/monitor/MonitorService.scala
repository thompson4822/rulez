package com.minutekey.monitor

import com.minutekey.model.LogRecord

/**
 * Created by steve on 7/16/14.
 */
trait MonitorService {
  def add(records: Seq[LogRecord])

  def checkKiosk(): Unit

  def checkHardwareStatus(): Unit

  def checkCancelClicks(): Unit

  def brassKeysLow(): Unit

  def checkForPurchases(): Unit

  def purchaseCount: Int

  def purchaseWindowHours: Int

  def cancelClicksExceeded: Option[String]

}
