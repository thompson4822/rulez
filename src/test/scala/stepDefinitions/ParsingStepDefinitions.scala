package stepDefinitions

import java.io.File
import java.util.{Calendar, Date}

import com.minutekey._
import com.minutekey.model.{LogRecord, ScreenRecord}
import cucumber.api.{DataTable, PendingException}
import cucumber.api.scala.{EN, ScalaDsl}
import org.mockito.Mockito._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions._
/**
 * Created by steve on 7/11/14.
 */
class TestFileSystem(fileContent: Seq[String]) extends FileSystem {
  val today = Calendar.getInstance().getTime

  override def logFiles: Map[Date, File] = Map(today -> new File("WUT"))

  override def read(file: File): Seq[String] = fileContent
}

class ParsingStepDefinitions extends ScalaDsl with EN with ShouldMatchers {

  var fileSystem: FileSystem = _

  var sut: LogReader = _

  Given("""^I have the following log:$"""){ (fileContent: DataTable) =>
    fileSystem = new TestFileSystem(fileContent.raw().flatten.toSeq)
    sut = new DefaultLogReader(fileSystem)
  }

  var log: Seq[LogRecord] = _

  When("""^I parse the log file$"""){ () =>
    log = sut.read
  }

  Then("""^I should see the screen "([^"]*)" was visited (\d+) times$"""){ (screenName: String, timesVisited: Int) =>
    log.collect { case record: ScreenRecord => record}.count(_.name == screenName) should be (timesVisited)
  }

  Then("""^any screen entries that are not "([^"]*)" should contain a session id$"""){ (screenName: String) =>
    val screenRecords = log.collect { case record: ScreenRecord => record }.filter(_.name != "screenName")
    screenRecords.map(_.sessionId).length should be (screenRecords.length)

  }

}
