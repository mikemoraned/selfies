package com.houseofmoran.selfies.faces

object VerticalFacePresence {
  def fromFaces(faces: Seq[DetectedFaceInContext]) : VerticalFacePresence = {
    val segmented = faces.groupBy(face => face.toVerticalSegment)
    VerticalFacePresence(
      segmented.get(LeftVertical),
      segmented.get(MiddleVertical),
      segmented.get(RightVertical))
  }
}

case class VerticalFacePresence(left: Option[Seq[DetectedFaceInContext]],
                                middle: Option[Seq[DetectedFaceInContext]],
                                right: Option[Seq[DetectedFaceInContext]])
