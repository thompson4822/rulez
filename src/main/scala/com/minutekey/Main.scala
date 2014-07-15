package com.minutekey

import java.io.{RandomAccessFile, File}
import java.nio.CharBuffer
import java.nio.channels.FileChannel.MapMode
import java.nio.charset.Charset
import java.nio.file.{Files, Path, FileSystems, Paths}
import java.nio.file.StandardWatchEventKinds._
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import akka.actor.{Props, ActorSystem, Actor, ActorRef}
import akka.event.{LoggingReceive, Logging}
import org.slf4j.LoggerFactory
import collection.JavaConversions._
import collection.mutable.{Map => MutableMap}

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
    val system = ActorSystem()
    val fsActor = system.actorOf(Props[FileSystemActor], "fileSystem")
    fsActor ! MonitorDir(Paths get "/home/steve/foo/bar")
/*
    TimeUnit.SECONDS.sleep(60)
    system.shutdown()
*/

  }
}

sealed trait FileSystemChange

case class Created(file: File) extends FileSystemChange

case class Modified(file: File) extends FileSystemChange

case class MonitorDir(path: Path)

class FileSystemActor extends Actor {
  def logger = LoggerFactory.getLogger("default")
  val log = Logging(context.system, this)
  val watchServiceTask = new WatchServiceTask(self)
  val watchThread = new Thread(watchServiceTask, "WatchService")

  // Need a mutable dictionary of file names to file size
  val knownFiles: MutableMap[File, Long] = MutableMap()

  override def preStart() {
    watchThread.setDaemon(true)
    watchThread.start()
  }

  override def postStop() {
    watchThread.interrupt()
  }

  def newContent(file: File): String = {
    val randomAccessFile = new RandomAccessFile(file, "r")
    val startPosition = knownFiles(file)
    val length = file.length() - startPosition
    val buff = randomAccessFile.getChannel.map(MapMode.READ_ONLY, startPosition, length)
    val enc = Charset.forName("ASCII")
    val chars: CharBuffer = enc.decode(buff)
    chars.toString
  }

  def receive = LoggingReceive {
    case MonitorDir(path) =>
      watchServiceTask.watch(path)
      // For each existing file in the directory, we need to call created to have it initially read and parsed
      logger.info(s"We're now going to monitor ${path.toString}")
      val directoryStream = Files.newDirectoryStream(path)
      val files = directoryStream.map(_.toFile)
      files.map(file => self ! Created(file))
    case Created(file) =>
      logger.info(s"The file '${file.toString}' was just created")
      knownFiles(file) = file.length()
      // Parse into individual case classes
    case Modified(file) =>
      // Get what changed in the file
      val addedContent = newContent(file)
      // Update the length of the file
      knownFiles(file) = file.length()
      logger.info(s"The file '${file.toString}' just had the following content added: \n$addedContent")

  }
}

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