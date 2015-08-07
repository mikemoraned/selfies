package com.houseofmoran.selfies.faces

import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.HaarCascadeDetector

import scala.collection.JavaConversions._

object Faces {
  def detectIn(urls: Seq[URL]): Map[URL,Seq[DetectedFaceInContext]] = {
    urls.foldLeft(Map[URL,Seq[DetectedFaceInContext]]())((map, url) => {
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
}
