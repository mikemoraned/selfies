package com.houseofmoran.selfies.tourist

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import com.houseofmoran.selfies.faces.Faces
import com.houseofmoran.selfies.{ImageFile, ImageFiles}
import org.scalatest._

class TouristSelfiesSpec extends FunSuite with Assertions {

  for((fileName, image) <- imagesInDir(new File("examples/touristselfies"))) {
    test(s"${fileName} should contain tourist selfies") {
      val faces = Faces.detectIn(image)
      assume(!faces.isEmpty)
      assert(TouristSelfie(faces))
    }
  }

  for((fileName, image) <- imagesInDir(new File("examples/chatapps"))) {
    test(s"${fileName} should not contain tourist selfies") {
      val faces = Faces.detectIn(image)
      assert(!TouristSelfie(faces))
    }
  }

  def imagesInDir(dir: File) =
    for {
      in <- ImageFiles(dir)
      ImageFile(base, ext) = in
      if !base.contains("faces")
    } yield (in.asFile.getName, loadImage(in.asFile))

  def loadImage(file: File) : BufferedImage = ImageIO.read(file)
}
