package com.houseofmoran.selfies.faces

object HorizontalFacePresence {
  def fromFaces(faces: Seq[DetectedFaceInContext]) : HorizontalFacePresence = {
    val segmented = faces.groupBy(face => face.toHorizontalSegment)
    HorizontalFacePresence(
      segmented.get(TopHorizontal),
      segmented.get(MiddleHorizontal),
      segmented.get(BottomHorizontal))
  }
}

case class HorizontalFacePresence(top: Option[Seq[DetectedFaceInContext]],
                                  middle: Option[Seq[DetectedFaceInContext]],
                                  bottom: Option[Seq[DetectedFaceInContext]])