package com.houseofmoran.spark.play.twitter

import org.scalatest._

class EmojiSpec extends FeatureSpec with GivenWhenThen {

  feature("can extract emoji usage from a String") {
    scenario("no emoji mentioned in String") {
      Given("string with no emoji")
      val s = "no emoji here"

      Then("an empty Set of emoji is found")
      assert(Emoji.findEmoji(s) === Set[Emoji]())
    }

    scenario("single emoji mentioned in String") {
      Given("single emoji")
      val s = "yay for emoji \uD83D\uDE04!"

      Then("a Set with single emoji is found")
      assert(Emoji.findEmoji(s) === Set[Emoji](Emoji("\uD83D\uDE04")))
    }

    scenario("multiple distinct emoji mentioned in String") {
      Given("two emoji")
      val s = "yay for emoji \uD83D\uDE04, so \uD83D\uDE02!"

      Then("a Set of two emoji is found")
      assert(Emoji.findEmoji(s) === Set[Emoji](Emoji("\uD83D\uDE04"), Emoji("\uD83D\uDE02")))
    }

    scenario("multiple repeated emoji mentioned in String") {
      Given("three emoji, one repeated")
      val s = "yay for emoji \uD83D\uDE04, so \uD83D\uDE02, \uD83D\uDE04!"

      Then("a Set of two emoji is found")
      assert(Emoji.findEmoji(s) === Set[Emoji](Emoji("\uD83D\uDE04"), Emoji("\uD83D\uDE02")))
    }
  }
}
