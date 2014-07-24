package stepDefinitions

import com.github.nscala_time.time.Imports._
import com.minutekey.model.{ScreenRecord, LogRecord, SurveyResponseRecord}
import com.minutekey.monitor.{DefaultMonitorService, MonitorService}
import cucumber.api.Scenario
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import stepDefinitions.GlobalTestState._
import stepDefinitions.TestUtils._

import scala.util.Random

/**
 * Created by steve on 7/23/14.
 */
class SurveyStepDefinitions  extends ScalaDsl with EN with Matchers {
  var monitorService: MonitorService = _

  val random = new Random(232323L)

  Before("@survey") { f: (Scenario) =>
    ticketGenerator = new TestTicketGenerator
    monitorService = new DefaultMonitorService(ticketGenerator)
  }

  When("""^there (is one|are multiple) surveys in a row that indicate the kiosk is not working$"""){ (howMany: String) =>
    val records = howMany match {
      case "is one" =>
        List (
          ScreenRecord("Attract Loop", DateTime.now, 30000, Some("session1")),
          SurveyResponseRecord(response="KioskNotWorking", sessionId = "session1", timeOfEntry = DateTime.now),
          ScreenRecord("Attract Loop", DateTime.now, 30000, Some("session2"))
        )
      case "are multiple" =>
        List (
          ScreenRecord("Attract Loop", DateTime.now, 30000, Some("session1")),
          SurveyResponseRecord(response="KioskNotWorking", sessionId = "session1", timeOfEntry = DateTime.now),
          ScreenRecord("Attract Loop", DateTime.now, 30000, Some("session2")),
          SurveyResponseRecord(response="KioskNotWorking", sessionId = "session2", timeOfEntry = DateTime.now),
          ScreenRecord("Attract Loop", DateTime.now, 30000, Some("session3"))
        )

    }
    monitorService.add(records)
  }

}
