package com.houseofmoran.zeitgeist

import java.awt.geom.Point2D
import java.awt.geom.Point2D.Double
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

import com.houseofmoran.selfies.faces._
import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.{DetectedFace, HaarCascadeDetector}

import scala.collection.JavaConversions._

import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.streaming._
import twitter4j.MediaEntity

object FacesCrawlerApp {

  case class DetectedFaceInContext(face: DetectedFace, img: BufferedImage) {
    def normalizedCentroid : Point2D = {
      val centroid = face.getBounds.calculateCentroid()
      new Double(centroid.getX / img.getWidth, centroid.getY / img.getHeight())
    }

    def toVerticalSegment : VerticalSegment = {
      val centroid = normalizedCentroid
      VerticalSegment.classify(centroid.getX)
    }

    def toHorizontalSegment : HorizontalSegment = {
      val centroid = normalizedCentroid
      HorizontalSegment.classify(centroid.getY)
    }
  }

  def detectFaces(entities: Array[MediaEntity]): Map[URL,Seq[DetectedFaceInContext]] = {
    entities.foldLeft(Map[URL,Seq[DetectedFaceInContext]]())((map, entity) => {
      val url = new URL(entity.getMediaURL)
      try {
        val bufferedImg = ImageIO.read(url)

        val img = ImageUtilities.createFImage(bufferedImg)

        val detector = new HaarCascadeDetector()

        val inContext = detector.detectFaces(img).map(face => {
          DetectedFaceInContext(face, bufferedImg)
        })

        return map.updated(url, inContext)
      }
      catch {
        case e: IOException => {
          return map
        }
      }
    })
  }

  case class VerticalFacePresence(left: Option[Seq[DetectedFaceInContext]],
                                  middle: Option[Seq[DetectedFaceInContext]],
                                  right: Option[Seq[DetectedFaceInContext]])

  def toVerticalFacePresence(faces: Seq[DetectedFaceInContext]) : VerticalFacePresence = {
    val segmented = faces.groupBy(face => face.toVerticalSegment)
    VerticalFacePresence(
      segmented.get(LeftVertical),
      segmented.get(MiddleVertical),
      segmented.get(RightVertical))
  }

  case class HorizontalFacePresence(top: Option[Seq[DetectedFaceInContext]],
                                    middle: Option[Seq[DetectedFaceInContext]],
                                    bottom: Option[Seq[DetectedFaceInContext]])

  def toHorizontalFacePresence(faces: Seq[DetectedFaceInContext]) : HorizontalFacePresence = {
    val segmented = faces.groupBy(face => face.toHorizontalSegment)
    HorizontalFacePresence(
      segmented.get(TopHorizontal),
      segmented.get(MiddleHorizontal),
      segmented.get(BottomHorizontal))
  }

  def singleFaceOnLeftOrRightOnly(faces: Seq[DetectedFaceInContext]) = {
    val facePresence = toVerticalFacePresence(faces)

    facePresence match {
      case VerticalFacePresence(Some(faces), None, None) => faces.length == 1
      case VerticalFacePresence(None, None, Some(faces)) => faces.length == 1
      case _ => false
    }
  }

  def faceInBottomOnly(faces: Seq[DetectedFaceInContext]) = {
    val facePresence = toHorizontalFacePresence(faces)

    facePresence match {
      case HorizontalFacePresence(None, None, Some(_)) => true
      case _ => false
    }
  }

  def filterFaces(urlToFaces: Map[URL, Seq[DetectedFaceInContext]]) = {
    urlToFaces.filter{case (url, faces) => {
      singleFaceOnLeftOrRightOnly(faces) && faceInBottomOnly(faces)
    }}
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
        val detectedFaces =
          if (mediaEntities != null)
            detectFaces(mediaEntities)
          else
            Map[URL,List[DetectedFaceInContext]]()

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
      .saveAsTextFiles("withMediaAndSingleFaceOnOneSideAndOnBottom-statusesWithUrls")

    ssc.start()
    ssc.awaitTermination()
  }
}
