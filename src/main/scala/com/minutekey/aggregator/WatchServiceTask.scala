package com.minutekey.aggregator

import java.nio.file.StandardWatchEventKinds._
import java.nio.file.{FileSystems, Path}

import akka.actor.ActorRef
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Created by steve on 7/16/14.
 */
class WatchServiceTask(notifyActor: ActorRef) extends Runnable {
  private val watchService = FileSystems.getDefault.newWatchService()
  def logger = LoggerFactory.getLogger("default")

  def watch(path: Path) =
    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)

  def run() {
    try {
      logger.debug("Waiting for log file changes...")
      while (!Thread.currentThread().isInterrupted) {
        val key = watchService.take()
        key.pollEvents() foreach {
          event =>
            val relativePath = event.context().asInstanceOf[Path]
            val path = key.watchable().asInstanceOf[Path].resolve(relativePath)
            event.kind() match {
              case ENTRY_CREATE =>
                notifyActor ! Created(path.toFile)
              case ENTRY_MODIFY =>
                notifyActor ! Modified(path.toFile)
              case other => logger.warn(s"Unknown event $other")
            }
        }
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
