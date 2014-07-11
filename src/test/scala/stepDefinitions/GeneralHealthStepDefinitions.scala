package stepDefinitions

import java.sql.Timestamp
import java.util.Calendar

import com.minutekey.{DefaultScreenMonitorService, ScreenMonitorService, TicketGenerator, HardwareMonitorService}
import com.minutekey.model.ScreenRecord
import cucumber.api.{DataTable, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import org.mockito.Mockito._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions._

/**
 * Created by steve on 7/10/14.
 */
class GeneralHealthStepDefinitions extends ScalaDsl with EN with ShouldMatchers with MockitoSugar {

  var timeout: Int = _

  val mockTicketGenerator = mock[TicketGenerator]

  var sut: ScreenMonitorService = _

  var hms: HardwareMonitorService = _

  var kioskType: String = _

  Given("""^our kiosk is (.*)$"""){ (kioskType: String) =>
    this.kioskType = kioskType
  }

  var keysInCart: Int = _

  Given("""^we're on key copy progress$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }

  Given("""^there are (\d+) keys$"""){ (arg0:Int) =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }

  implicit def table2screenRecords(dataTable: DataTable): Seq[ScreenRecord] =
    dataTable.raw().tail.map(columns => ScreenRecord(columns(0), columns(1).toInt))


  var screenRecords: Seq[ScreenRecord] = _

  Given("""^the following screen records:$"""){ (screens:DataTable) =>
    screenRecords = screens
  }

  var currentScreen: ScreenRecord = _

  Given("""^the current screen is "([^"]*)"$"""){ (screenName: String) =>
    currentScreen = screenRecords.find(_.name == screenName).get
  }

  var elapsed: Int = _

  private def now = new Timestamp(Calendar.getInstance().getTimeInMillis)

  private def adjustWhenStarted(screen: ScreenRecord, secondsAgo: Int): ScreenRecord = {
    val start = Calendar.getInstance()
    start.add(Calendar.SECOND, -secondsAgo)
    screen.copy(timeOfEntry = new Timestamp(start.getTimeInMillis))
  }

  When("""^time elapsed is (\d+) seconds$"""){ (elapsedSeconds: Int) =>
    currentScreen = adjustWhenStarted(currentScreen, elapsedSeconds)
    sut = new DefaultScreenMonitorService(currentScreen, mockTicketGenerator)
  }

  //set up Before to avoid timer getting called on 1 sec case
  Then("""^a ticket (should|should not) be sent$"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "should") 1 else 0
    Thread.sleep(currentScreen.timeoutSeconds*1200)
    verify(mockTicketGenerator, times(timesCalled)).create(currentScreen.name)
  }

  Then("""^the expected time on this screen is (\d+) minutes$"""){ (arg0:Int) =>

  }


  /*
    Given("""^there are (\d+) keys$"""){ (keys:Int) =>
      keysInCart = keys
    }
  */

/*
  Given("""^there have been more than two consecutive "([^"]*)" surveys$"""){ (arg0:String) =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }
*/

  var ticketGenerated: Boolean = _

/*
  When("""^time elapsed is > (\d+) minutes$"""){ (elapsed: Int) =>
    ticketGenerated = elapsed > timeout
  }

  Then("""^create a ticket$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    ticketGenerated should be (true)
  }

  Then("""^the expected time on this screen is (\d+) minutes$"""){ (minutes: Int) =>
    true should be (true)
  }

  Then("""^I should generate a ticket$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    throw new PendingException()
  }
*/

  var device: String = _

  Given("""^we have USB attached hardware devices such as bill collectors and credit card readers$"""){ (dev: String) =>
    device = dev
  }

  var disconnectCount: Int = _

  Given("""^the disconnect count is (\d+)$"""){ (cnt:Int) =>
    disconnectCount = cnt
  }

  Then("""^whether to generate a ticket is (yes|no) $"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "yes") 1 else 0
    hms.checkHardwareStatus
    verify(mockTicketGenerator, times(timesCalled)).create(device)
  }

}
