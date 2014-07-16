package com.minutekey.parser

import java.util.Date

import com.minutekey.model.LogRecord

/**
 * Created by steve on 7/16/14.
 */
trait LogParser {
  def parse(date: Date, lines: Seq[String]): Seq[LogRecord]
}
