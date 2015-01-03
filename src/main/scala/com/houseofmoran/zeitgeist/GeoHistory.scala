package com.houseofmoran.zeitgeist

import org.json4s.JValue
import org.json4s.JsonDSL._

class GeoHistory extends Snapshotable {
  override def toJSON(): JValue = {
    List()
  }
}
