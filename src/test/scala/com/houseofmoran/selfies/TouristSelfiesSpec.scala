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
    for(fileName <- dir.listFiles())
      yield (fileName, loadImage(fileName))

  def loadImage(fileName: File) : BufferedImage = {
    new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
  }
}
