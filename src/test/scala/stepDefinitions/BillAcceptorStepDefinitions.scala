package stepDefinitions

import com.minutekey.TicketGenerator
import com.minutekey.model.{BillAcceptorCassetteRemovedRecord, BillAcceptorConnectedRecord}
import com.minutekey.monitor.{DefaultMonitorService, MonitorService}
import cucumber.api.{Scenario, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import org.joda.time.DateTime
import org.scalatest.Matchers
import GlobalTestState._

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
    val records = List(
      BillAcceptorConnectedRecord("Acceptor connected", DateTime.now, "Bob"),
      BillAcceptorConnectedRecord("Acceptor connected", DateTime.now, "Bob")
    )
    monitorService.add(records)
  }

  When("""^the bill acceptor cassette has been detached more than once today$"""){ () =>
    val records = List(
      BillAcceptorCassetteRemovedRecord("Cassette is Removed", DateTime.now, "Bob"),
      BillAcceptorCassetteRemovedRecord("Cassette is Removed", DateTime.now, "Bob")
    )
    monitorService.add(records)
  }

}
