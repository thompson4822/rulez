package stepDefinitions

import java.sql.Timestamp
import java.util.Calendar
import com.minutekey._
import cucumber.api.{DataTable, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.Scenario
import org.mockito.Mockito._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions._


class PricingStepDefinitions extends ScalaDsl with EN with ShouldMatchers with MockitoSugar {

  val mockTicketGenerator = mock[TicketGenerator]
  var ms: MonitorService = _

  var machineGrade: String =  _

  var noPurchaseMockTicketGenerator = mock[TicketGenerator]
  Before("@clickTest") { f: Scenario =>
    noPurchaseMockTicketGenerator = mock[TicketGenerator]
    ms = new DefaultMonitorService(noPurchaseMockTicketGenerator)
  }

  Given("""^a (A|B) machine$"""){ (grade: String) =>
    machineGrade = grade
  }

  Then("""^if (\d+) purchases by (\d+):(\d+), I should generate a ticket: (yes|no)$"""){ (purchases: Int, hour: Int, minute: Int, ticketSent:String) =>
    val timesCalled = if (ticketSent == "yes") 1 else 0
    ms.purchaseCount(0)
    ms.checkForPurchases
    verify(noPurchaseMockTicketGenerator, times(timesCalled)).create("No purchases made today.")
  }
}

