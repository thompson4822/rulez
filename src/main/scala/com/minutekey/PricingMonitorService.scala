package com.minutekey

trait PricingMonitorService {
  var purchaseCnt: Int = _
  var purchaseWindowHrs: Int = _
  def ticketGenerator: TicketGenerator
  def checkForPurchases: Unit = {
    if (purchaseCnt == 0)
      ticketGenerator.create("No purchases made today.")
  }
  def purchaseCount(cnt: Int) = { purchaseCnt = cnt }
  def purchaseWindowHours(hours: Int) = { purchaseWindowHrs = hours }
}

class DefaultPricingMonitorService(val ticketGenerator: TicketGenerator) extends PricingMonitorService {


}

