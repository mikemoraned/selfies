package com.houseofmoran.zeitgeist

import org.json4s.JValue

trait Snapshotable {
  def toJSON(): JValue
}
