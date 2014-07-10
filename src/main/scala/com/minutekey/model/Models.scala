package com.minutekey.model

import java.sql.Timestamp
import java.util.Calendar

case class ScreenRecord(name: String, timeOfEntry: Timestamp, timeoutSeconds: Int) {
}

object ScreenRecord {
  def apply(name: String, timeoutSeconds: Int): ScreenRecord = {
    val now = new Timestamp(Calendar.getInstance().getTimeInMillis)
    ScreenRecord(name, now, timeoutSeconds)
  }
}

