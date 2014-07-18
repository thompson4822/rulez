package com.minutekey

import java.nio.file.Paths

import akka.actor.{ActorSystem, Props}
import com.minutekey.aggregator.{FileSystemActor, MonitorDir}
import org.slf4j.LoggerFactory
import com.typesafe.config._

/**
 * Created by steve on 7/9/14.
 */

// TODO - This should probably be derived from a properties file in the future
object Configuration {
  val logPath = "/var/log/minutekey"

  val conf: Config = ConfigFactory.load()
  var brassLowAmount: Int = conf.getInt("brassLowAmount")
  var billAcceptorDisconnectLimit: Int = conf.getInt("billAcceptorDisconnectLimit")
  val daysOfInterest = conf.getInt("daysOfInterest")
  val monitorFrequency = conf.getInt("monitorFrequency")
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











