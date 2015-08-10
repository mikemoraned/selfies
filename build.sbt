name := """selfies"""

version := "1.0"

// needed to run apps, due incompatible versions of scala between sbt and spark
// see http://stackoverflow.com/a/28616509
// Note: only saw this problem when upgrading to version 1.4.0 of spark from 1.2.0
fork := true

scalaVersion := "2.11.4"

resolvers += "OpenIMAJ maven releases repository" at "http://maven.openimaj.org"

resolvers += "OpenIMAJ maven snapshots repository" at "http://snapshots.openimaj.org"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.1",
  "org.apache.spark" %% "spark-sql" % "1.4.1",
  "org.apache.spark" %% "spark-streaming" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.4.1",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "org.mortbay.jetty" % "servlet-api" % "3.0.20100224",
  "org.eclipse.jetty" % "jetty-server" % "9.2.6.v20141205",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "ch.hsr" % "geohash" % "1.0.10")

libraryDependencies += "com.twelvemonkeys.common" % "common-lang" % "3.0.2"

libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-core" % "3.0.2"

libraryDependencies += "org.openimaj" % "image-processing" % "1.3.1"

libraryDependencies += "org.openimaj" % "faces" % "1.3.1"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test")
