package com.houseofmoran.selfies.faces

import java.awt.geom.Point2D
import java.awt.geom.Point2D.Double
import java.awt.image.BufferedImage

import org.openimaj.image.processing.face.detection.DetectedFace

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