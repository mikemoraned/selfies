package com.houseofmoran.selfies

import java.net.URL

import com.houseofmoran.selfies.faces._
import com.houseofmoran.selfies.tourist.TouristSelfie
import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.streaming._

object FacesCrawlerApp {

  def filterFaces(urlToFaces: Map[URL, Seq[DetectedFaceInContext]]) = {
    urlToFaces.filter{case (url, faces) => TouristSelfie(faces) }
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("FacesCrawlerApp").setMaster("local[*]")
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
        val mediaEntities = status.getMediaEntities
        val mediaEntityURLs : Seq[URL] =
          if (mediaEntities == null)
            Seq.empty
          else
            mediaEntities.map(e => new URL(e.getMediaURL))
        val detectedFaces = Faces.detectIn(mediaEntityURLs)

        !filterFaces(detectedFaces).isEmpty
      })
      .window(Minutes(1), Minutes(1))

    selfieStatuses
      .map(status => {
        val id = status.getId().toString
        val user = status.getUser().getScreenName()
        val url = s"https://twitter.com/${user}/status/${id}"
        val mediaEntities = status.getMediaEntities
        val entitiesUrls =
          if (mediaEntities == null)
            ""
          else
            mediaEntities.map(e => e.getMediaURL).mkString(",")
        (id, url, entitiesUrls)
      })
      .saveAsTextFiles("../selfies-data/withMediaAndSingleLargeFaceOnOneSideAndOnBottom-statusesWithUrls")

    ssc.start()
    ssc.awaitTermination()
  }
}
