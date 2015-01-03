package com.houseofmoran.zeitgeist

import org.apache.spark.rdd.RDD
import org.json4s.JValue
import org.json4s.JsonDSL._

class GeoHistory extends Snapshotable {
  override def toJSON(): JValue = {
    List()
  }

  def newCounts(rdd: RDD[(String, Int)]): Unit = {
    rdd.collect().map(println)
  }
}
