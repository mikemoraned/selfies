package com.houseofmoran.selfies.faces

import java.io.File
import javax.imageio.ImageIO

import com.houseofmoran.selfies.{ImageFile, ImageFiles}
import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.face.detection.HaarCascadeDetector
import org.openimaj.image.processing.face.util.SimpleDetectedFaceRenderer

import scala.collection.JavaConversions._

object FaceDetectionApp {

  def illustrateFaces(in: File, out: File) = {
    val img = ImageIO.read(in)

    val detectedFaces = Faces.detectIn(img)
    val illustration = ImageUtilities.createMBFImage(img, false)
    for(detectedFace <- detectedFaces) {
      println(s"proportion of image: ${detectedFace.sizeAsProportionOfImage()}")
      new SimpleDetectedFaceRenderer()
        .drawDetectedFace(illustration, 2, detectedFace.face)
    }

    ImageUtilities.write(illustration, out)
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
      illustrateFaces(in.asFile, out)
    }
  }
}
