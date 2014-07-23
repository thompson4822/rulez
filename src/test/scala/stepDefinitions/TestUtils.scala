package stepDefinitions

import com.minutekey.model.LogRecord

/**
 * Created by steve on 7/22/14.
 */
object TestUtils {
  def multiplyRecord[A <: LogRecord](record: A, count: Int): List[A] = {
    def multiplyRecordRec(accum: List[A], count: Int): List[A] =
      count match {
        case 0 => accum
        case x => multiplyRecordRec(record :: accum, count - 1)
      }
    multiplyRecordRec(Nil, count)
  }
}
