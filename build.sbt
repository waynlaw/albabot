name := "albabot"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += "central" at "http://repo1.maven.org/maven2/"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "org.apache.httpcomponents" % "httpclient" % "4.5.3",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
