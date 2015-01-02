package com.houseofmoran.zeitgeist

import java.io.PrintWriter
import org.apache.spark.rdd.RDD
import twitter4j.Status

import org.json4s._

case class TweetSample() {
  var possibleSample : Option[Seq[Status]] = None

  def newWindow(window: RDD[(Long, Status)]) : Unit = {
    possibleSample = Some(window.takeSample(true, 10).map{ case (_, status) => status })
  }

  def toJSON() : JValue = {
    possibleSample match {
      case Some(sample) => {
        val entries =
          for (status : Status <- sample if status.getGeoLocation() != null)
            yield toJSON(status)

        new JArray( entries.toList )
      }
      case None => new JArray(List())
    }
  }

  private def toJSON(status: Status) : JValue = {
     new JObject(List(
       ("id", JString(status.getId().toString)),
       ("location", new JObject(List(
         ("lat", JDouble(status.getGeoLocation.getLatitude)),
         ("lon", JDouble(status.getGeoLocation.getLongitude))
       )))
     ))
  }
}
