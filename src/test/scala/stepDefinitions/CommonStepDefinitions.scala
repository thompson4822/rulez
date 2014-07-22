package stepDefinitions

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar

import GlobalTestState._

/**
 * Created by steve on 7/22/14.
 */
class CommonStepDefinitions extends ScalaDsl with EN with Matchers with MockitoSugar  {

  Given("pending") { () =>
    //throw new PendingException()
  }

  Then("""^a "([^"]*)" ticket should be generated$"""){ (ticketMessage: String) =>
    ticketGenerator.messagesSent should contain (ticketMessage)
  }

}
