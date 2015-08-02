package com.houseofmoran.selfies.faces

object VerticalSegment {
  val leftBound = 0.33
  val middleBound = leftBound * 2

  def classify(x: Double): VerticalSegment = {
    if (x < 0.0 || x > 1.0) {
      throw new IllegalArgumentException("out of bounds: " + x)
    }

    if (x >= 0.0 && x < leftBound) {
      Left
    }
    else if (x >= leftBound && x < middleBound) {
      Middle
    }
    else {
      Right
    }
  }
}
sealed class VerticalSegment
case object Left extends VerticalSegment
case object Middle extends VerticalSegment
case object Right extends VerticalSegment
