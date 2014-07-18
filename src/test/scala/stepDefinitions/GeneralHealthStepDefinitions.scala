package stepDefinitions

import java.sql.Timestamp
import java.util.{Date, Calendar}

import com.minutekey._
import com.minutekey.model._
import com.minutekey.monitor.DefaultMonitorService
import com.minutekey.parser._
import cucumber.api.{DataTable, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.Scenario
import org.mockito.Mockito._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions._

/**
 * Created by steve on 7/10/14.
 */
class GeneralHealthStepDefinitions extends ScalaDsl with EN with ShouldMatchers with MockitoSugar {
  var logData: Seq[String] = Nil

  var sut: LogParser = _

//  Given("""^I have the following log:$"""){ (fileContent: DataTable) =>
//    logData = fileContent.raw().flatten.toSeq
//    sut = new DefaultLogParser
//  }
//  When("""^I parse the log data"""){ () =>
  //    val today = new Date()
  //    log = sut.parse(today, logData)
  //  }

  var log: Seq[LogRecord] = _

  var timeout: Int = _

  val mockTicketGenerator = mock[TicketGenerator]

/*
  var sms: ScreenMonitorService = _
*/

  var monitorService: DefaultMonitorService = _

  var kioskType: String = _

  var ticketGenerated: Boolean = _



  Given("""^our kiosk is (.*)$"""){ (kioskType: String) =>
    this.kioskType = kioskType
  }

  var keysInCart: Int = _

  Given("""^we're on key copy progress$"""){ () =>
    //// Express the Regexp above with the code you wish you had
    //throw new PendingException()
  }

  Given("""^there are (\d+) keys$"""){ (arg0:Int) =>
    //// Express the Regexp above with the code you wish you had
    //throw new PendingException()
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
    ???
/*
    val start = Calendar.getInstance()
    start.add(Calendar.SECOND, -secondsAgo)
    screen.copy(timeOfEntry = new Timestamp(start.getTimeInMillis))
*/
  }

  When("""^time elapsed is (\d+) seconds$"""){ (elapsedSeconds: Int) =>
    ???
/*
    currentScreen = adjustWhenStarted(currentScreen, elapsedSeconds)
    sms = new DefaultScreenMonitorService(currentScreen, mockTicketGenerator)
*/
  }

  //set up Before to avoid timer getting called on 1 sec case
  Then("""^a ticket (should|should not) be sent$"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "should") 1 else 0
    Thread.sleep(currentScreen.timeoutSeconds*1200)
    verify(mockTicketGenerator, times(timesCalled)).create(currentScreen.name)
  }

  Then("""^the expected time on this screen is (\d+) minutes$"""){ (arg0:Int) =>

  }

  //
  //Hardware disconnect test
  //

  var device: String = _

  Given("""^we have USB attached hardware devices (Bill Collector|Card Reader)$"""){ (dev: String) =>
    device = dev
    monitorService = new DefaultMonitorService(mockTicketGenerator)
  }

  Then("""^whether to generate a ticket is (yes|no) $"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "yes") 1 else 0
    monitorService.checkHardwareStatus
    verify(mockTicketGenerator, times(timesCalled)).create(device)
  }

  //
  //Brass Key test
  //

  Given("""^a kiosk has brass keys$"""){ () =>
    monitorService = new DefaultMonitorService(mockTicketGenerator)
  }

  var brassKeyCnt: Int = _

  Given("""^the number of keys remaining is (\d+)$"""){ (cnt:Int) =>
    brassKeyCnt = cnt
  }

  Then("""^whether to generate a brass low ticket is (yes|no)$"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "yes") 1 else 0
    monitorService.brassKeysLow()
    verify(mockTicketGenerator, times(timesCalled)).create("Brass keys low")
  }

  //
  // Cancel clicks test
  //

  var clkMockTicketGenerator = mock[TicketGenerator]
  Before("@clickTest") { f: Scenario =>
    clkMockTicketGenerator = mock[TicketGenerator]
    monitorService = new DefaultMonitorService(clkMockTicketGenerator)
  }

  var cancelClicksCnt: Int = _

  Given("""^the number of cancel button clicks is (\d+)$"""){ (cnt:Int) =>
    cancelClicksCnt = cnt
  }

  Then("""^whether to generate a ticket is (yes|no)$"""){ (ticketSent: String) =>
    val timesCalled = if (ticketSent == "yes") 1 else 0
    monitorService.checkCancelClicks
    verify(clkMockTicketGenerator, times(timesCalled)).create("Cash Payment - excessive cancels")
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
}

