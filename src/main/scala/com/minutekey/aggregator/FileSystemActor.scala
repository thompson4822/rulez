package com.minutekey.aggregator

import java.io.{File, RandomAccessFile}
import java.nio.CharBuffer
import java.nio.channels.FileChannel.MapMode
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.Date

import akka.actor.Actor
import akka.event.{Logging, LoggingReceive}
import com.minutekey.{DefaultMonitorService, MonitorService}
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
  val monitor: MonitorService = new DefaultMonitorService

  // Mutable dictionary of files and their last known size
  val knownFiles: MutableMap[File, Long] = MutableMap()

  override def preStart() {
    watchThread.setDaemon(true)
    watchThread.start()
  }

  override def postStop() {
    watchThread.interrupt()
  }

  // Gets any changes in the given file, or the whole file if it was just created
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
      val directoryStream = Files.newDirectoryStream(path)
      // Grab all the files for this directory
      // TODO - Only grab the last XX days of log file data
      val files = directoryStream.flatMap { item =>
        if(!Files.isDirectory(item)) Some(item) else None
      }.map(_.toFile)
      files.map(file => self ! Created(file))
    case Created(file) =>
      // Set the initial size to 0 so that the whole file is read in
      knownFiles(file) = 0
      // Trigger a modified event to parse the file
      // TODO - If the file is before today, parse it and add to monitor without kicking off the ticket checking logic
      self ! Modified(file)
    case Modified(file) =>
      // Get the changes made to the file
      val addedContent = new StringOps(newContent(file)).lines.toList
      // Update the length of the file
      knownFiles(file) = file.length()
      // Parse the modifications
      val records = logParser.parse(new Date(file.lastModified()), addedContent)
      monitor.add(records)
  }
}
