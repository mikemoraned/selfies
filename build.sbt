import NativePackagerKeys._

packageArchetype.java_application

name := """selfies"""

version := "1.0"

mainClass in Compile := Some("com.houseofmoran.zeitgeist.TwitterVisApp")

// needed to run apps, due incompatible versions of scala between sbt and spark
// see http://stackoverflow.com/a/28616509
// Note: only saw this problem when upgrading to version 1.4.0 of spark from 1.2.0
fork := true

scalaVersion := "2.11.4"
  
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.0",
  "org.apache.spark" %% "spark-sql" % "1.4.0",
  "org.apache.spark" %% "spark-streaming" % "1.4.0",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.4.0",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "org.mortbay.jetty" % "servlet-api" % "3.0.20100224",
  "org.eclipse.jetty" % "jetty-server" % "9.2.6.v20141205",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "ch.hsr" % "geohash" % "1.0.10")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test")
