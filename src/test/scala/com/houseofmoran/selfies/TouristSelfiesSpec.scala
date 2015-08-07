package com.houseofmoran.selfies

import java.awt.image.BufferedImage
import java.io.File

import org.scalatest._

class TouristSelfiesSpec extends FunSuite {

  for((fileName, image) <- imagesInDir(new File("examples/good"))) {
    test(s"${fileName} should contain tourist selfies") {
      assert(1 === 2)
    }
  }

  def imagesInDir(dir: File) =
    for {
      in <- ImageFiles(dir)
      ImageFile(base, ext) = in
      if !base.contains("faces")
    } yield (in.asFile.getName, loadImage(in.asFile))

  def loadImage(fileName: File) : BufferedImage = {
    new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
  }
}
