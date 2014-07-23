package stepDefinitions

import com.minutekey.model.{BillAcceptorCassetteRemovedRecord, BillAcceptorConnectedRecord}
import com.minutekey.monitor.{DefaultMonitorService, MonitorService}
import cucumber.api.Scenario
import cucumber.api.scala.{EN, ScalaDsl}
import org.joda.time.DateTime
import org.scalatest.Matchers
import stepDefinitions.GlobalTestState._
import TestUtils._

/**
 * Created by steve on 7/18/14.
 */
class BillAcceptorStepDefinitions extends ScalaDsl with EN with Matchers {
  var monitorService: MonitorService = _

  Before("@billAcceptor") { f: (Scenario) =>
    ticketGenerator = new TestTicketGenerator
    monitorService = new DefaultMonitorService(ticketGenerator)
  }

  When("""^the bill acceptor has re-connected several times today$"""){ () =>
    val records = multiplyRecord(BillAcceptorConnectedRecord("Acceptor connected", DateTime.now, "Bob"), 2)
    monitorService.add(records)
  }

  When("""^the bill acceptor cassette has been detached more than once today$"""){ () =>
    val records = multiplyRecord(BillAcceptorCassetteRemovedRecord("Cassette is Removed", DateTime.now, "Bob"), 2)
    monitorService.add(records)
  }

}
