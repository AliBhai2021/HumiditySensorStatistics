name := "HumiditySensorStatistics"

version := "0.1"

scalaVersion := "2.11.12"


lazy val akkaVersion = "2.5.21"
lazy val scalaTestVersion = "3.2.0-SNAP10"
lazy val sparkCassandraConnector = "2.0.0-M2"
lazy val phantom          = "1.28.16"
lazy val boopickle        = "1.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "com.typesafe.scala-logging" %% "scala-logging"       % "3.1.0"
)
