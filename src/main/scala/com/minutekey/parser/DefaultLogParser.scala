package com.minutekey.parser

import java.util.Date

import com.minutekey.model._
import RecordParsers._

/**
 * Created by steve on 7/16/14.
 */
class DefaultLogParser extends LogParser {

  override def parse(date: Date, lines: Seq[String]): Seq[LogRecord] = {
    lines flatMap {
      case PageEntryParser(time, payload) => Some(ScreenRecord(date, time, payload))
      case BillAcceptorDisconnectedParser(time, payload) => Some(BillAcceptorDisconnectedRecord(date, time, payload))
      case BillAcceptorConnectedParser(time, payload) => Some(BillAcceptorConnectedRecord(date, time, payload))
      case SurveyResponseParser(time, payload) => Some(SurveyResponseRecord(date, time, payload))
      case ButtonClickParser(time, payload) => Some(ButtonClickRecord(date, time, payload))
      case _ => None
    }
  }
}
