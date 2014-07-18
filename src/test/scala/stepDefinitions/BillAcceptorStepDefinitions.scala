package stepDefinitions

import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

/**
 * Created by steve on 7/18/14.
 */
class BillAcceptorStepDefinitions extends ScalaDsl with EN with ShouldMatchers with MockitoSugar {
  When("""^the bill acceptor has re-connected several times today$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }
  Then("""^a "([^"]*)" ticket should be generated$"""){ (arg0:String) =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }
  When("""^the bill acceptor cassette has been detached more than once today$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }

}
