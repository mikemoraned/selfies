package com.houseofmoran.zeitgeist

import org.apache.spark.rdd.RDD
import org.json4s._
import twitter4j.Status
import org.json4s.JsonDSL._

case class TweetSample() extends Snapshotable {
  var possibleSample : Option[Seq[Status]] = None

  def newWindow(window: RDD[(Long, Status)]) : Unit = {
    possibleSample = Some(window.takeSample(true, 1000).map{ case (_, status) => status })
  }

  def toJSON() : JValue = {
    possibleSample match {
      case Some(sample) => {
        for (status : Status <- sample if status.getGeoLocation() != null)
          yield toJSON(status)
      }
      case None => List()
    }
  }

  private def toJSON(status: Status) : JValue = {
    ("id" -> status.getId().toString) ~
    ("location" ->
        ("lat" -> status.getGeoLocation.getLatitude) ~
        ("lon" -> status.getGeoLocation.getLongitude))
  }
}
