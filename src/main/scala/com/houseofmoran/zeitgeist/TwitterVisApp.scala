package com.houseofmoran.zeitgeist

import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.streaming._
import org.eclipse.jetty.server.handler.{HandlerList, ResourceHandler}
import org.eclipse.jetty.server.{Handler, Server}

import scala.util.Properties

object TwitterVisApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterVisApp").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val windowLength = Seconds(10)
    implicit val ssc = new StreamingContext(sc, windowLength)

    val twitterStream =
      if (args.length > 0) {
        println("Reading auth from file")
        TwitterStreamSource.streamFromAuthIn(args(0))
      }
      else {
        println("Reading auth from env")
        TwitterStreamSource.streamFromEnv()
      }

    val sample = new TweetSample
    val geoHistory = new GeoHistory

    val sampleHandler = new SnapshotHandler(Map(
      ("sample" -> sample), ("geohistory" -> geoHistory)))

    val stream = twitterStream.
      filter(status => status.getGeoLocation() != null).
      map(status => (status.getId, status))

    stream.foreachRDD( rdd => sample.newWindow(rdd) )

    val server = new Server(Properties.envOrElse("PORT", "8080").toInt)

    val resources = new ResourceHandler()
    resources.setWelcomeFiles(Array[String]("index.html"))
    resources.setResourceBase("./src/main/resources")

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler]( resources, sampleHandler ))

    server.setHandler(handlers)
    server.start()

    ssc.start()
    ssc.awaitTermination()
  }
}
