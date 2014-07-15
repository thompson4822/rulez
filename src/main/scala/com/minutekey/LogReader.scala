package com.minutekey

import java.io.File
import java.util.Date

import com.minutekey.model._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.util.matching.Regex

/**
 * Created by steve on 7/11/14.
 */
trait LogReader {
  def read: Seq[LogRecord]
}

import util.matching.Regex

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

class DefaultLogReader(fileSystem: FileSystem) extends LogReader {
  import RecordParsers._

  def readFile(date: Date, file: File): Seq[LogRecord] = {
    val lines = fileSystem.read(file)
    lines flatMap {
      case PageEntryParser(time, payload) => Some(ScreenRecord(date, time, payload))
      case BillAcceptorDisconnectedParser(time, payload) => Some(BillAcceptorDisconnectedRecord(date, time, payload))
      case BillAcceptorConnectedParser(time, payload) => Some(BillAcceptorConnectedRecord(date, time, payload))
      case SurveyResponseParser(time, payload) => Some(SurveyResponseRecord(date, time, payload))
      case _ => None
    }
  }

  override def read: Seq[LogRecord] = {
    val files = fileSystem.logFiles
    files.map(record => readFile(record._1, record._2)).flatten.toSeq
  }
}

trait LogParser {
  def parse(date: Date, lines: Seq[String]): Seq[LogRecord]
}

class DefaultLogParser extends LogParser {
  import RecordParsers._

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