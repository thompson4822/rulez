package com.minutekey.aggregator

import java.io.File
import java.nio.file.Path

/**
 * Created by steve on 7/16/14.
 */
sealed trait FileSystemChange

case class Created(file: File) extends FileSystemChange
case class Modified(file: File) extends FileSystemChange

case class MonitorDir(path: Path)