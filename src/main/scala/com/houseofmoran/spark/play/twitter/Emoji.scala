package com.houseofmoran.spark.play.twitter

object Emoji {
  def findEmoji(s: String) : Set[Emoji] = {
    val emojiRange = """[\x{1f000}-\x{1ffff}]"""
    val regex = s"($emojiRange)".r

    (for(regex(e) <- regex.findAllIn(s)) yield Emoji(e)).toSet
  }
}

case class Emoji(chars: String)
