name := "rulez"

version := "1.0"

scalaVersion := "2.10.2"

autoScalaLibrary := false

scalaHome := Some(file("/home/steve/Applications/scala"))

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.10.2",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"
)

seq(cucumberSettings : _*)

cucumberStepsBasePackage := "stepDefinitions"

cucumberHtmlReport := true

cucumberJunitReport := true

cucumberJsonReport := true

