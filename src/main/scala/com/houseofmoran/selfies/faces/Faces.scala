package com.houseofmoran.selfies.faces

import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.HaarCascadeDetector

import scala.collection.JavaConversions._

object Faces {

  def detectIn(bufferedImg: BufferedImage) : Seq[DetectedFaceInContext] = {
    val img = ImageUtilities.createFImage(bufferedImg)

    val detector = new HaarCascadeDetector()

    detector.detectFaces(img).map(face => {
      DetectedFaceInContext(face, bufferedImg)
    })
  }

  def detectIn(urls: Seq[URL]): Map[URL,Seq[DetectedFaceInContext]] = {
    urls.foldLeft(Map[URL,Seq[DetectedFaceInContext]]())((map, url) => {
      try {
        val bufferedImg = ImageIO.read(url)
        return map.updated(url, detectIn(bufferedImg))
      }
      catch {
        case e: IOException => {
          return map
        }
      }
    })
  }
}
