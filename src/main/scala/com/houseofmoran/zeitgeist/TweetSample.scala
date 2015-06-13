package com.houseofmoran.zeitgeist

import org.apache.spark.rdd.RDD
import org.json4s._
import twitter4j.Status
import org.json4s.JsonDSL._

case class TweetSample() extends Snapshotable {
  var possibleSample : Option[Seq[Status]] = None

  def newWindow(window: RDD[(Long, Status)]) : Unit = {
    possibleSample = Some(window.map{ case (_, status) => status }.collect())
  }

  def toJSON() : JValue = {
    possibleSample match {
      case Some(sample) => {
        for (status : Status <- sample)
          yield toJSON(status)
      }
      case None => List()
    }
  }

  private def toJSON(status: Status) : JValue = {
    val id = status.getId().toString
    val user = status.getUser().getId
    ("id" -> id) ~
    ("user" -> user) ~
    ("url" -> s"https://twitter.com/${user}/status/${id}")
  }
}
