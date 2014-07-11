package com.minutekey

import java.io.File
import java.util.Date

import com.minutekey.model._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by steve on 7/11/14.
 */
trait LogReader {
  def read: Seq[LogRecord]
}

import util.matching.Regex

class DefaultLogReader(fileSystem: FileSystem) extends LogReader {
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
   */
  def parserWithIdentifier(identifier: String): Regex =
    ("^" + timeParser + " " + identifier + " " + payloadParser + "$").r

  val PageEntryParser = parserWithIdentifier("DEBUG - Page Entry:")
  val BillAcceptorDisconnectedParser = parserWithIdentifier("DEBUG - Page Entry:")
  val BillAcceptorConnectedParser = parserWithIdentifier("DEBUG - BillAcceptorConnectedEvent:")
  val SurveyResponseParser = parserWithIdentifier("DEBUG - SurveyResponse:")
  val UnknownRecordParser = parserWithIdentifier(".*")

  def readFile(date: Date, file: File): Seq[LogRecord] = {
    val lines = fileSystem.read(file)
    lines map {
      case PageEntryParser(time, payload) => ScreenRecord(date, time, payload)
      case BillAcceptorDisconnectedParser(time, payload) => BillAcceptorDisconnectedRecord(date, time, payload)
      case BillAcceptorConnectedParser(time, payload) => BillAcceptorConnectedRecord(date, time, payload)
      case SurveyResponseParser(time, payload) => SurveyResponseRecord(date, time, payload)
      case UnknownRecordParser(time, payload) => UnknownRecord(date, time, payload)
    }
  }

  override def read: Seq[LogRecord] = {
    val files = fileSystem.logFiles
    files.map(record => readFile(record._1, record._2)).flatten.toSeq
  }
}