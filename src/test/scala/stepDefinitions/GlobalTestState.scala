package stepDefinitions

import com.minutekey.TicketGenerator

import scala.collection.mutable.ListBuffer

/**
 * Created by steve on 7/22/14.
 */
class TestTicketGenerator extends TicketGenerator {
  var messagesSent: ListBuffer[String] = new ListBuffer

  override def create(msg: String): Unit = messagesSent += msg
}

object GlobalTestState {
  var ticketGenerator: TestTicketGenerator = _
}
