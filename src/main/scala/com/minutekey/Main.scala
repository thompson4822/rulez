package com.minutekey

import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import com.minutekey.aggregator.{FileSystemActor, MonitorDir}
import org.slf4j.LoggerFactory

/**
 * Created by steve on 7/9/14.
 */

// TODO - This should probably be derived from a properties file in the future
object Configuration {
  val logPath = "/var/log/minutekey"

  val daysOfInterest = 7 // Originally we are just going to look back 1 week in time
}

object Main {
  def logger = LoggerFactory.getLogger("default")

  // Note that the approach used here was adapted from http://www.nurkiewicz.com/2013/04/watchservice-combined-with-akka-actors.html
  def main(args: Array[String]) {
    val system = ActorSystem()
    val fsActor = system.actorOf(Props[FileSystemActor], "fileSystem")
    fsActor ! MonitorDir(Paths get Configuration.logPath)
  }
}











