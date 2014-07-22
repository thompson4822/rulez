package com.minutekey.parser

import java.util.Date

import com.minutekey.model._
import RecordParsers._

/**
 * Created by steve on 7/16/14.
 */
class DefaultLogParser extends LogParser {

  // TODO - The following is just for debugging. Should be moved into the actual log monitor
  var pageEntryCount: Int = _
  var billAcceptorDisconnectedCount: Int = _
  var billAcceptorConnectedCount: Int = _
  var surveyResponseCount: Int = _
  var buttonClickCount: Int = _

  def statistics: String = s"Page entries: $pageEntryCount, Cash Disconnect: $billAcceptorDisconnectedCount, Cash Connect: $billAcceptorConnectedCount, " +
    s"Survey Responses: $surveyResponseCount, Button Clicks: $buttonClickCount"

  override def parse(date: Date, lines: Seq[String]): Seq[LogRecord] = {
    lines flatMap {
      case InvalidKeyTypeParser(time, payload) =>
        Some(InvalidKeyTypeRecord(date, time, payload))
      case PageEntryParser(time, payload) =>
        pageEntryCount += 1
        Some(ScreenRecord(date, time, payload))
      case BillAcceptorDisconnectedParser(time, payload) =>
        billAcceptorDisconnectedCount += 1
        Some(BillAcceptorDisconnectedRecord(date, time, payload))
      case BillAcceptorCassetteRemovedParser(time, payload) =>
        Some(BillAcceptorCassetteRemovedRecord(date, time, payload))
      case BillAcceptorConnectedParser(time, payload) =>
        billAcceptorConnectedCount += 1
        Some(BillAcceptorConnectedRecord(date, time, payload))
      case SurveyResponseParser(time, payload) =>
        surveyResponseCount += 1
        Some(SurveyResponseRecord(date, time, payload))
      case ButtonClickParser(time, payload) =>
        buttonClickCount += 1
        Some(ButtonClickRecord(date, time, payload))
      case _ => None
    }
  }
}
