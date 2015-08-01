package com.houseofmoran.selfies.faces

import java.io.File
import javax.imageio.ImageIO

import scala.collection.JavaConversions._

import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.keypoints.{FKEFaceDetector, KEDetectedFace}
import org.openimaj.image.processing.face.detection.{DetectedFace, CLMFaceDetector, CLMDetectedFace, HaarCascadeDetector}
import org.openimaj.image.processing.face.util.{KEDetectedFaceRenderer, CLMDetectedFaceRenderer, SimpleDetectedFaceRenderer}

object FaceDetectionApp {
  def main(args: Array[String]): Unit = {
    val bufferedImg = ImageIO.read(new File(args(0)))
    val img = ImageUtilities.createFImage(bufferedImg)

    val mbf = ImageUtilities.createMBFImage(bufferedImg, false)

    // A simple Haar-Cascade face detector
    val det1 = new HaarCascadeDetector()
    val faces = det1.detectFaces(img)
    for(face <- faces) {
      new SimpleDetectedFaceRenderer()
        .drawDetectedFace(mbf, 10, face)
    }

    ImageUtilities.write(mbf, new File(args(1)))
  }
}
