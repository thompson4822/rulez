package com.minutekey

import java.io.File
import java.util.Date
import scala.io.Source

/**
 * Created by steve on 7/11/14.
 */
trait FileSystem {
  def logFiles: Map[Date, File]
  def read(filename: String): Seq[String]
}

class DefaultFileSystem {
  def logFiles: Map[Date, File] = {
    ???
  }

  def read(filename: String): Seq[String] = {
    ???
  }
}
