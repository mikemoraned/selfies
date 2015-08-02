package com.houseofmoran.selfies.faces

object HorizontalSegment {
  val topBound = 0.33
  val middleBound = topBound * 2

  def classify(y: Double): HorizontalSegment = {
    if (y < 0.0 || y > 1.0) {
      throw new IllegalArgumentException("out of bounds: " + y)
    }

    if (y >= 0.0 && y < topBound) {
      TopHorizontal
    }
    else if (y >= topBound && y < middleBound) {
      MiddleHorizontal
    }
    else {
      BottomHorizontal
    }
  }
}
sealed class HorizontalSegment
case object TopHorizontal extends HorizontalSegment
case object MiddleHorizontal extends HorizontalSegment
case object BottomHorizontal extends HorizontalSegment
