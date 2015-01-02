package com.houseofmoran.spark.play.twitter

import scala.collection.JavaConversions._

object TwitterWordUsage {
  def mapWordsToEmoji(s: String) : Map[String, Emoji] = {

    val words = s.split(" ").toList
    val emoji = Emoji.findEmoji(s)

    val pairs = words.flatMap((word) => emoji.map( emoji => word -> emoji))

    Map[String, Emoji]() ++ pairs
  }
}
