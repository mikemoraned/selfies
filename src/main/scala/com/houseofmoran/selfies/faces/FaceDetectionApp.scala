package com.houseofmoran.selfies.faces

import java.io.File
import javax.imageio.ImageIO

import com.houseofmoran.selfies.{ImageFile, ImageFiles}
import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.HaarCascadeDetector
import org.openimaj.image.processing.face.util.SimpleDetectedFaceRenderer

import scala.collection.JavaConversions._

object FaceDetectionApp {

  def showFaces(in: File, out: File) = {
    val bufferedImg = ImageIO.read(in)
    val img = ImageUtilities.createFImage(bufferedImg)

    val mbf = ImageUtilities.createMBFImage(bufferedImg, false)

    // A simple Haar-Cascade face detector
    val det1 = new HaarCascadeDetector()
    val faces = det1.detectFaces(img)
    for(face <- faces) {
      new SimpleDetectedFaceRenderer()
        .drawDetectedFace(mbf, 10, face)
    }

    ImageUtilities.write(mbf, out)
  }

  def main(args: Array[String]): Unit = {
    val dirName = new File(args(0))
    for {
      in <- ImageFiles(dirName)
      ImageFile(base, ext) = in
      if !base.contains("faces")
    } {
      val out = new File(s"${base}.faces.${ext}")
      println(s"${in} -> ${out}")
      showFaces(in.asFile, out)
    }
  }
}
