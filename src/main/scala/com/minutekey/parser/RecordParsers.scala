package com.minutekey.parser

import scala.util.matching.Regex

/**
 * Created by steve on 7/16/14.
 */
object RecordParsers {
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  val timeParser = """(\d{2}:\d{2}:\d{2}\.\d{3})"""
  val payloadParser = """\[([^\]]*)\]"""

  // TODO - log reader will have to ascertain from the file it is reading what the date is so that this can be factored in with the time on the individual lines.

  /*
  Examples - These can be used for testing!
  09:27:00.661 DEBUG - Page Entry: [screen=Startup]
  09:27:00.661 DEBUG - Page Entry: [screen=Startup]
  13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0]
  13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]
  13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=]
  09:27:08.926 DEBUG - ButtonClick: [screen=Attract Loop, sessionid=fda6e558-75c2-45a3-a425-674cb1e703ea, button=Touch To Begin, level=0, message=]
   */
  def parserWithIdentifier(identifier: String): Regex =
    ("^" + timeParser + " " + identifier + " " + payloadParser + "$").r

  val PageEntryParser = parserWithIdentifier("DEBUG - Page Entry:")
  val BillAcceptorDisconnectedParser = parserWithIdentifier("DEBUG - Page Entry:")
  val BillAcceptorConnectedParser = parserWithIdentifier("DEBUG - BillAcceptorConnectedEvent:")
  val SurveyResponseParser = parserWithIdentifier("DEBUG - SurveyResponse:")
  val ButtonClickParser = parserWithIdentifier("DEBUG - ButtonClick:")
  val UnknownRecordParser = parserWithIdentifier(".*")

}
