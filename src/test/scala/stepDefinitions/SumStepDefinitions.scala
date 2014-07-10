package stepDefinitions

import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by steve on 7/10/14.
 */
class SumStepDefinitions extends ScalaDsl with EN with ShouldMatchers {
  var numbers: List[Int] = Nil

  Given("""^the number (\d+)$"""){ (number: Int) =>
    numbers = number :: numbers
  }

  When("""^I add these together$"""){ () =>

  }

  Then("""^I should get the number (\d+)$"""){ (sum: Int) =>
    numbers.sum should be (sum)
  }

}
