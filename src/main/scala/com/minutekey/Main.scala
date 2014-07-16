package com.minutekey

import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import com.minutekey.aggregator.{FileSystemActor, MonitorDir}
import org.slf4j.LoggerFactory

/**
 * Created by steve on 7/9/14.
 */

// This should probably be derived from a properties file in the future
object Configuration {
  val logPath = "/var/log/minutekey"
}

object Main {
  def logger = LoggerFactory.getLogger("default")

  def main(args: Array[String]) {
    val system = ActorSystem()
    val fsActor = system.actorOf(Props[FileSystemActor], "fileSystem")
    fsActor ! MonitorDir(Paths get Configuration.logPath)
  }
}











