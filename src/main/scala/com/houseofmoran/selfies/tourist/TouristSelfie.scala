package com.houseofmoran.selfies.tourist

import com.houseofmoran.selfies.faces.{HorizontalFacePresence, VerticalFacePresence, DetectedFaceInContext}

object TouristSelfie {

  def apply(faces: Seq[DetectedFaceInContext]): Boolean = {
    faceIsALargePartOfTheImage(faces) && singleFaceOnLeftOrRightOnly(faces) && faceInBottomOnly(faces)
  }

  def faceIsALargePartOfTheImage(faces: Seq[DetectedFaceInContext]) = {
    faces.forall(f => {
      val proportion = f.sizeAsProportionOfImage()
      proportion >= 0.10 && proportion <= 0.20
    })
  }

  def singleFaceOnLeftOrRightOnly(faces: Seq[DetectedFaceInContext]) = {
    val facePresence = VerticalFacePresence.fromFaces(faces)

    facePresence match {
      case VerticalFacePresence(Some(faces), None, None) => faces.length == 1
      case VerticalFacePresence(None, None, Some(faces)) => faces.length == 1
      case _ => false
    }
  }

  def faceInBottomOnly(faces: Seq[DetectedFaceInContext]) = {
    val facePresence = HorizontalFacePresence.fromFaces(faces)

    facePresence match {
      case HorizontalFacePresence(None, None, Some(_)) => true
      case _ => false
    }
  }
}
