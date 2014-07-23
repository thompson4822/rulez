package stepDefinitions

import com.github.nscala_time.time.Imports._
import com.minutekey.model._
import com.minutekey.monitor.DefaultMonitorService
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.{DataTable, Scenario}
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import org.slf4j.LoggerFactory
import stepDefinitions.GlobalTestState._
import stepDefinitions.TestUtils._

import scala.collection.JavaConversions._

/**
 * Created by steve on 7/10/14.
 */
class GeneralHealthStepDefinitions extends ScalaDsl with EN with Matchers with MockitoSugar {
  def logger = LoggerFactory.getLogger("default")

  var monitorService: DefaultMonitorService = _

  implicit def table2screenRecords(dataTable: DataTable): Seq[ScreenRecord] =
    dataTable.raw().tail.map(columns => ScreenRecord(columns(0), columns(1).toInt))

  var screenRecords: Seq[ScreenRecord] = _

  Before("@generalHealth"){ f: Scenario =>
    ticketGenerator = new TestTicketGenerator
    monitorService = new DefaultMonitorService(ticketGenerator)
  }

  When("""^there (?:are|is) (\d) cancel button (?:click|clicks) on screen "([^"]*)"$"""){ (clickCount: Int, screen: String) =>
    val records = multiplyRecord(ButtonClickRecord(screen = screen, button = "Cancel", sessionId = "", timeOfEntry = DateTime.now), clickCount)
    monitorService.add(records)
  }

  When("""^the customer's last screen was "([^"]*)"$"""){ (lastScreen: String) =>
    val records = List(
      ScreenRecord(screen=lastScreen, timeOfEntry = DateTime.now, timeoutSeconds = 0, sessionId = Some("session1")),
      ScreenRecord(screen="Remove Key", timeOfEntry = DateTime.now, timeoutSeconds = 0, sessionId = Some("session2"))
    )
    monitorService.add(records)
  }

  When("""^the screen "([^"]*)" (does|doesn't) transition within its timeout period$"""){ (screen:String, transitionIndicator: String) =>
    val timeOfEntry = if(transitionIndicator == "doesn't") DateTime.now - 13.minutes else DateTime.now - 20.seconds
    val records = List(
      ScreenRecord(screen=screen, timeOfEntry = timeOfEntry, timeoutSeconds = 0, sessionId = Some("session2"))
    )
    monitorService.add(records)
  }
}

