package com.minutekey.model

import java.util.{Date}
import com.github.nscala_time.time.Imports._

trait RecordUtils {
  def dateTimeFor(date: Date, time: String): DateTime =
    new DateTime(s"${date.getYear + 1900}-${date.getMonth + 1}-${date.getDate} $time")

  def payloadToMap(payload: String): Map[String, String] = {
    val keyValues = payload.split(',').map(_.trim)
    keyValues.map { keyValue =>
      val pair = keyValue.split('=')
      if(pair.length < 2)
        None
      else
        Some(pair(0) -> pair(1))
    }.flatten.toMap
  }
}

sealed trait LogRecord {
  def timeOfEntry: DateTime
}

case class ScreenRecord(name: String, timeOfEntry: DateTime, timeoutSeconds: Int, sessionId: Option[String] = None, attributes: Map[String, String] = Map()) extends LogRecord {
}

object ScreenRecord extends RecordUtils {
  def apply(name: String, timeoutSeconds: Int): ScreenRecord =
    ScreenRecord(name, DateTime.now, timeoutSeconds)

  def apply(date: Date, time: String, payload: String): ScreenRecord = {
    val attributes = payloadToMap(payload)
    ScreenRecord(name = attributes("screen"), timeOfEntry = dateTimeFor(date, time), timeoutSeconds = 3, sessionId = attributes.get("sessionid"), attributes)
  }
}

/*
  13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0]
  13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]
  13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=]
  09:27:08.926 DEBUG - ButtonClick: [screen=Attract Loop, sessionid=fda6e558-75c2-45a3-a425-674cb1e703ea, button=Touch To Begin, level=0, message=]
  12:46:46.798 DEBUG - KeyEject: [Stack=9, sessionid=507d4452-589f-4fa4-894a-e8860f9aca63, Quantity=30, SKU=KSCJMB00001BRAS, level=0, message=]
  00:57:11.749 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0]
 */

case class ButtonClickRecord(screen: String, button: String, sessionId: String, timeOfEntry: DateTime) extends LogRecord {

}

object ButtonClickRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): ButtonClickRecord = {
    val attributes = payloadToMap(payload)
    ButtonClickRecord(screen = attributes("screen"), button = attributes("button"), sessionId = attributes("sessionid"), timeOfEntry = dateTimeFor(date, time))
  }
}

case class BillAcceptorDisconnectedRecord(description: String, timeOfEntry: DateTime, username: String) extends LogRecord

object BillAcceptorDisconnectedRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): BillAcceptorDisconnectedRecord = {
    val attributes = payloadToMap(payload)
    BillAcceptorDisconnectedRecord(description = attributes("Description"), timeOfEntry = dateTimeFor(date, time), username = attributes("username"))
  }
}

case class BillAcceptorConnectedRecord(description: String, timeOfEntry: DateTime, username: String) extends LogRecord

object BillAcceptorConnectedRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): BillAcceptorConnectedRecord = {
    val attributes = payloadToMap(payload)
    BillAcceptorConnectedRecord(description = attributes("Description"), timeOfEntry = dateTimeFor(date, time), username = attributes("username"))
  }
}

case class KeyEjectRecord(SKU: String, timeOfEntry: DateTime, quantity: Int) extends LogRecord

object KeyEject extends RecordUtils {
  def apply(date: Date, time: String, payload: String): KeyEjectRecord = {
    val attributes = payloadToMap(payload)
    KeyEjectRecord(SKU = attributes("SKU"), timeOfEntry = dateTimeFor(date, time), quantity = attributes("Quantity").toInt)
  }
}

case class SurveyResponseRecord(response: String, sessionId: String, timeOfEntry: DateTime) extends LogRecord

object SurveyResponseRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): SurveyResponseRecord = {
    val attributes = payloadToMap(payload)
    SurveyResponseRecord(response = attributes("SurveyResponse"), sessionId = attributes("sessionid"), timeOfEntry = dateTimeFor(date, time))
  }
}

case class UnknownRecord(sessionId: Option[String] = None, timeOfEntry: DateTime) extends LogRecord

object UnknownRecord extends RecordUtils {
  def apply(date: Date, time: String, payload: String): UnknownRecord = {
    val attributes = payloadToMap(payload)
    UnknownRecord(sessionId = attributes.get("sessionid"), timeOfEntry = dateTimeFor(date, time))
  }
}

