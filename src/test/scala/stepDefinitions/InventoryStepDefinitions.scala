package stepDefinitions

import com.minutekey.model.InvalidKeyTypeRecord
import com.minutekey.monitor.{DefaultMonitorService, MonitorService}
import cucumber.api.{Scenario, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import org.joda.time.DateTime
import org.scalatest.Matchers
import GlobalTestState._

/**
 * Created by steve on 7/22/14.
 */
class InventoryStepDefinitions extends ScalaDsl with EN with Matchers {
  var monitorService: MonitorService = _

  Before("@inventory") { f: (Scenario) =>
    ticketGenerator = new TestTicketGenerator
    monitorService = new DefaultMonitorService(ticketGenerator)
  }

  When("""^too many keys could not be identified today$"""){ () =>
    val records = List(
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch"),
      InvalidKeyTypeRecord(sessionId="session", timeOfEntry = DateTime.now, message="ouch")
    )
    monitorService.add(records)
  }

}
