name := "rulez"

version := "1.0"

scalaVersion := "2.10.3"

autoScalaLibrary := false

//scalaHome := Some(file("/home/steve/Applications/scala"))

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.10.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "commons-logging" % "commons-logging" % "1.2",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"
)


seq(cucumberSettings : _*)

cucumberStepsBasePackage := "stepDefinitions"

cucumberHtmlReport := true

cucumberJunitReport := true

cucumberJsonReport := true

