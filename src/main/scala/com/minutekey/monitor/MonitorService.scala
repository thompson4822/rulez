package com.minutekey.monitor

import com.minutekey.model.LogRecord

/**
 * Created by steve on 7/16/14.
 */
trait MonitorService {
  def add(records: Seq[LogRecord])

  def checkBillAcceptorConnects(records: List[LogRecord]): Unit

  def checkBillAcceptorCassetteRemovals(records: List[LogRecord]): Unit

  def checkSessionConclusion(records: List[LogRecord]): Unit

  def checkScreenTransition(records: List[LogRecord]): Unit

  def checkKioskNotWorking(records: List[LogRecord]): Unit

  def checkKiosk(): Unit

  def checkHardwareStatus(records: List[LogRecord]): Unit

  def checkCancelClicks(records: List[LogRecord]): Unit

  def brassKeysLow(records: List[LogRecord]): Unit

  def checkForPurchases(records: List[LogRecord]): Unit

  def purchaseCount: Int

  def purchaseWindowHours: Int

  def checkUnidentifiedKeys(records: List[LogRecord]): Unit
}
