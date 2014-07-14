package stepDefinitions

import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.ShouldMatchers


/**
 * Created by steve on 7/10/14.
 */
class GeneralStepDefinitions extends ScalaDsl with EN with ShouldMatchers {
  Given("pending") { () =>
    //throw new PendingException()
  }
}
