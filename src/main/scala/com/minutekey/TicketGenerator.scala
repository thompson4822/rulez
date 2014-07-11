package com.minutekey

trait TicketGenerator {
  def create(msg: String): Unit
}

class DefaultTicketGenerator extends TicketGenerator {
  override def create(msg: String): Unit = {}
}