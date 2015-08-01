package com.houseofmoran.zeitgeist

import ch.hsr.geohash.GeoHash
import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.streaming._
import org.eclipse.jetty.server.handler.{HandlerList, ResourceHandler}
import org.eclipse.jetty.server.{Handler, Server}
import twitter4j.GeoLocation

import scala.util.Properties

object SelfiesCrawlerApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SelfiesCrawlerApp").setMaster("local[*]")
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

    val selfieStatuses = twitterStream.
      filter(status => {
        status.getMediaEntities != null && status.getMediaEntities().length > 0
      })
      .window(Minutes(1), Minutes(1))

    selfieStatuses
      .map(status => {
        val id = status.getId().toString
        val user = status.getUser().getId
        val url = s"https://twitter.com/${user}/status/${id}"
        val mediaEntities = status.getMediaEntities
        val entitiesUrls =
          if (mediaEntities == null)
            ""
          else
            mediaEntities.map(e => e.getMediaURL).mkString(",")
        (id, url, entitiesUrls)
      })
      .saveAsTextFiles("withMedia-statusesWithUrls")

    ssc.start()
    ssc.awaitTermination()
  }
}
