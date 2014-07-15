package com.minutekey

import java.nio.file.{FileSystems, Paths}
import java.nio.file.StandardWatchEventKinds._
import java.sql.Timestamp
import akka.actor.ActorRef
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import collection.JavaConversions._

/**
 * Created by steve on 7/9/14.
 */

// Create a few domain models here that we want to populate with information from the logs
case class Screen(name: String, time: Timestamp, session: String)

case class Key(product: String, cost: BigDecimal, lastMinuteOffer: Boolean)

case class Cart(keys: List[Key])

case class CustomerTransaction(session: String, screens: List[Screen], cart: Cart, paymentMethod: String)

// Create parser
object ModelParser {

}

object Main {
  def logger = LoggerFactory.getLogger("default")
  def main(args: Array[String]) {
    val watchService = FileSystems.getDefault.newWatchService()
    Paths.get("/home/steve/foo/bar").register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

    while(true) {
      val key = watchService.take()
      key.pollEvents() foreach { event =>
        event.kind() match {
          case ENTRY_CREATE => logger.info("Dude, a file was just created!")
          case ENTRY_DELETE => logger.info("Dude, a file was just deleted!")
          case ENTRY_MODIFY => logger.info("Dude, somebody just changed a file!")
          case x =>
            logger.info(s"Unknown event $x")
          //logger.warn(s"Unknown event $x")
        }
      }
      key.reset()
    }}
  }

class WatchServiceTask2(notifyActor: ActorRef) extends Runnable {
  private val watchService = FileSystems.getDefault.newWatchService()
  def logger = LoggerFactory.getLogger("default")

  def run() {
    try {
      while (!Thread.currentThread().isInterrupted) {
        val key = watchService.take()
        //coming soon...
        key.reset()
      }
    } catch {
      case e: InterruptedException =>
        logger.info("Interrupting, bye!")
    } finally {
      watchService.close()
    }
  }
}