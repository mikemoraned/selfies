package com.houseofmoran.selfies.faces

object VerticalSegment {
  val leftBound = 0.33
  val middleBound = leftBound * 2

  def classify(x: Double): VerticalSegment = {
    if (x < 0.0 || x > 1.0) {
      throw new IllegalArgumentException("out of bounds: " + x)
    }

    if (x >= 0.0 && x < leftBound) {
      LeftVertical
    }
    else if (x >= leftBound && x < middleBound) {
      MiddleVertical
    }
    else {
      RightVertical
    }
  }
}
sealed class VerticalSegment
case object LeftVertical extends VerticalSegment
case object MiddleVertical extends VerticalSegment
case object RightVertical extends VerticalSegment
