package com.houseofmoran.selfies

import java.io.File

case class ImageFile(base: String, extension: String) {
  def asFile : File =
    new File(s"${base}.${extension}")
}

object ImageFiles {
  val ImageFilesPattern = "(.+)\\.(...)$".r

  def apply(dirName: File) : Seq[ImageFile] = {
    val files = for (f <- dirName.listFiles() if f.isFile) yield f
    files.flatMap(f => {
      f.getPath match {
        case ImageFilesPattern(base, ext) => {
          List(ImageFile(base, ext))
        }
        case _ => List()
      }
    })
  }
}
