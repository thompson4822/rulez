name := "rulez"

version := "1.0"

scalaVersion := "2.10.3"

autoScalaLibrary := false

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.10.2",
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j"    % "2.2.3",
  "commons-logging" % "commons-logging" % "1.2",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "com.typesafe" % "config" % "1.2.1"
)


seq(cucumberSettings : _*)

cucumberStepsBasePackage := "stepDefinitions"

cucumberHtmlReport := true

cucumberJunitReport := true

cucumberJsonReport := true

