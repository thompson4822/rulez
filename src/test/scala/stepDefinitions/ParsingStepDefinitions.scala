package stepDefinitions

import java.util.Date

import com.minutekey.model.{LogRecord, ScreenRecord}
import com.minutekey.parser.{DefaultLogParser, LogParser}
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

import scala.collection.JavaConversions._
/**
 * Created by steve on 7/11/14.
 */
class ParsingStepDefinitions extends ScalaDsl with EN with Matchers {

  var logData: Seq[String] = Nil

  var sut: LogParser = _

  Given("""^I have the following log:$"""){ (fileContent: DataTable) =>
    logData = fileContent.raw().flatten.toSeq
    sut = new DefaultLogParser
  }

  var log: Seq[LogRecord] = _

  When("""^I parse the log data"""){ () =>
    val today = new Date()
    log = sut.parse(today, logData)
  }

  Then("""^I should see the screen "([^"]*)" was visited (\d+) times$"""){ (screenName: String, timesVisited: Int) =>
    log.collect { case record: ScreenRecord => record}.count(_.screen == screenName) should be (timesVisited)
  }

  Then("""^any screen entries that are not "([^"]*)" should contain a session id$"""){ (screenName: String) =>
    val screenRecords = log.collect { case record: ScreenRecord => record }.filter(_.screen != screenName)
    screenRecords.flatMap(_.sessionId).length should be (screenRecords.length)

  }
}
