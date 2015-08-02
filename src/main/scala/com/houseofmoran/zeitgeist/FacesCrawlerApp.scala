package com.houseofmoran.zeitgeist

import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.{DetectedFace, HaarCascadeDetector}

import scala.collection.JavaConversions._

import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.streaming._
import twitter4j.MediaEntity

object FacesCrawlerApp {

  def detectFaces(entities: Array[MediaEntity]): Map[URL,List[DetectedFace]] = {
    entities.foldLeft(Map[URL,List[DetectedFace]]())((map, entity) => {
      val url = new URL(entity.getMediaURL)
      try {
        val bufferedImg = ImageIO.read(url)

        val img = ImageUtilities.createFImage(bufferedImg)

        val detector = new HaarCascadeDetector()

        return map.updated(url, detector.detectFaces(img).toList)
      }
      catch {
        case e: IOException => {
          return map
        }
      }
    })
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
        mediaEntities != null &&
          mediaEntities.length > 0 &&
          !detectFaces(mediaEntities).isEmpty
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
      .saveAsTextFiles("withMediaAndFaces-statusesWithUrls")

    ssc.start()
    ssc.awaitTermination()
  }
}
