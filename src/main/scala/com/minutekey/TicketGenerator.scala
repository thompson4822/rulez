package com.minutekey

import org.slf4j.LoggerFactory

trait TicketGenerator {
  def create(msg: String): Unit
}

class DefaultTicketGenerator extends TicketGenerator {
  def logger = LoggerFactory.getLogger("default")

  override def create(msg: String): Unit =
    logger.error(msg)
}