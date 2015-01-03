package com.houseofmoran.zeitgeist

import org.apache.spark.rdd.RDD
import org.json4s.JValue
import org.json4s.JsonDSL._

import scala.collection.immutable.HashMap

class GeoHistory extends Snapshotable {
  var countForGeoHash = new HashMap[String, Int]()

  override def toJSON(): JValue = {
    countForGeoHash
  }

  def newCounts(rdd: RDD[(String, Int)]): Unit = {
    var latest = new HashMap[String, Int]()
    latest ++= rdd.collect()
    countForGeoHash = countForGeoHash.merged(latest)({ case ((k,v1),(_,v2)) => (k,v1+v2) })
  }
}
