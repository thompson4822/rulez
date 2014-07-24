package stepDefinitions

import com.minutekey._
import com.minutekey.monitor.DefaultMonitorService
import cucumber.api.{PendingException, Scenario}
import cucumber.api.scala.{EN, ScalaDsl}
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar


class PricingStepDefinitions extends ScalaDsl with EN with Matchers with MockitoSugar {

  val mockTicketGenerator = mock[TicketGenerator]
  var ms: DefaultMonitorService = _

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
    throw new PendingException()
/*
    val timesCalled = if (ticketSent == "yes") 1 else 0
    ms.checkForPurchases
    verify(noPurchaseMockTicketGenerator, times(timesCalled)).create("No purchases made today.")
*/
  }
}

