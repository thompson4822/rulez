package com.minutekey.aggregator

import java.io.{File, RandomAccessFile}
import java.nio.CharBuffer
import java.nio.channels.FileChannel.MapMode
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.Date

import akka.actor.Actor
import akka.event.{Logging, LoggingReceive}
import com.minutekey.parser.DefaultLogParser
import org.slf4j.LoggerFactory

import scala.collection.mutable.{Map => MutableMap}
import scala.collection.immutable.StringOps
import scala.collection.JavaConversions._

/**
 * Created by steve on 7/16/14.
 */
class FileSystemActor extends Actor {
  def logger = LoggerFactory.getLogger("default")
  val log = Logging(context.system, this)
  val watchServiceTask = new WatchServiceTask(self)
  val watchThread = new Thread(watchServiceTask, "WatchService")
  val logParser = new DefaultLogParser

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
      val addedContent = new StringOps(newContent(file)).lines.toList
      logParser.parse(new Date(file.lastModified()), addedContent)
      // Update the length of the file
      knownFiles(file) = file.length()
      logger.info(s"The file '${file.toString}' just had the following content added: \n$addedContent")

  }
}
