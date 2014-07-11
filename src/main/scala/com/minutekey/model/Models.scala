package com.minutekey.model

import java.sql.Timestamp
import java.util.{Date, Calendar}

trait RecordUtils {
  def timestampFor(date: Date, time: String): Timestamp = {
    ???
  }

  def payloadToMap(payload: String): Map[String, String] = {
    ???
  }
}

sealed trait LogRecord

case class ScreenRecord(name: String, timeOfEntry: Timestamp, timeoutSeconds: Int, sessionId: Option[String] = None) extends LogRecord {
}

object ScreenRecord extends RecordUtils {
  def apply(name: String, timeoutSeconds: Int): ScreenRecord = {
    val now = new Timestamp(Calendar.getInstance().getTimeInMillis)
    ScreenRecord(name, now, timeoutSeconds)
  }

  def apply(date: Date, time: String, payload: String): ScreenRecord = {
    val attributes = payloadToMap(payload)
    ScreenRecord(name = attributes("screen"), timeOfEntry = timestampFor(date, time), timeoutSeconds = 3, sessionId = attributes.get("sessionid"))
  }
}

/*
  13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0]
  13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]
  13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=]
 */

case class BillAcceptorDisconnectedRecord(description: String, timeOfEntry: Timestamp, username: String) extends LogRecord

object BillAcceptorDisconnectedRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): BillAcceptorDisconnectedRecord = {
    ???
  }
}

case class BillAcceptorConnectedRecord(description: String, timeOfEntry: Timestamp, username: String) extends LogRecord

object BillAcceptorConnectedRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): BillAcceptorConnectedRecord = {
    ???
  }
}

case class SurveyResponseRecord(response: String, sessionId: String, timeOfEntry: Timestamp) extends LogRecord

object SurveyResponseRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): SurveyResponseRecord = {
    ???
  }
}

case class UnknownRecord(sessionId: Option[String] = None, timeOfEntry: Timestamp) extends LogRecord

object UnknownRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): UnknownRecord = {
    ???
  }
}

