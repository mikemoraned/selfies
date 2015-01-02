package com.houseofmoran.spark.play.twitter

import org.scalatest._

class TwitterWordUsageSpec extends FeatureSpec with GivenWhenThen {

  feature("can relate words to emoji in String") {
    scenario("no emoji mentioned in String") {
      Given("string with no emoji")
      val s = "no emoji here"

      Then("an empty Map of words is produced")
      assert(TwitterWordUsage.mapWordsToEmoji(s) === Map[String, Emoji]())
    }

    scenario("single emoji mentioned in String") {
      Given("single emoji")
      val s = "yay for emoji \uD83D\uDE04!"

      Then("a Map with a (word, emoji) pair for every word in string, including emoji")
      assert(TwitterWordUsage.mapWordsToEmoji(s) === Map[String, Emoji](
        "yay"           -> Emoji("\uD83D\uDE04"),
        "for"           -> Emoji("\uD83D\uDE04"),
        "emoji"         -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE04!" -> Emoji("\uD83D\uDE04")
      ))
    }

    scenario("multiple distinct emoji mentioned in String") {
      Given("two emoji")
      val s = "yay for emoji \uD83D\uDE04, so \uD83D\uDE02!"

      Then("a Map with a (word, emoji) pair for every word in string, including emoji")
      assert(TwitterWordUsage.mapWordsToEmoji(s) === Map[String, Emoji](
        "yay"           -> Emoji("\uD83D\uDE04"),
        "for"           -> Emoji("\uD83D\uDE04"),
        "emoji"         -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE04," -> Emoji("\uD83D\uDE04"),
        "so"            -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE02!" -> Emoji("\uD83D\uDE04"),

        "yay"           -> Emoji("\uD83D\uDE02"),
        "for"           -> Emoji("\uD83D\uDE02"),
        "emoji"         -> Emoji("\uD83D\uDE02"),
        "\uD83D\uDE04," -> Emoji("\uD83D\uDE02"),
        "so"            -> Emoji("\uD83D\uDE02"),
        "\uD83D\uDE02!" -> Emoji("\uD83D\uDE02")
      ))
    }

    scenario("multiple repeated emoji mentioned in String") {
      Given("three emoji, one repeated")
      val s = "yay for emoji \uD83D\uDE04, so \uD83D\uDE02, \uD83D\uDE04!"

      Then("a Map with every *distinct* (word, emoji) pair for every word in string, including emoji")
      assert(TwitterWordUsage.mapWordsToEmoji(s) === Map[String, Emoji](
        "yay"           -> Emoji("\uD83D\uDE04"),
        "for"           -> Emoji("\uD83D\uDE04"),
        "emoji"         -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE04," -> Emoji("\uD83D\uDE04"),
        "so"            -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE02," -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE04!" -> Emoji("\uD83D\uDE04"),

        "yay"           -> Emoji("\uD83D\uDE02"),
        "for"           -> Emoji("\uD83D\uDE02"),
        "emoji"         -> Emoji("\uD83D\uDE02"),
        "\uD83D\uDE04," -> Emoji("\uD83D\uDE02"),
        "so"            -> Emoji("\uD83D\uDE02"),
        "\uD83D\uDE02," -> Emoji("\uD83D\uDE02"),
        "\uD83D\uDE04!" -> Emoji("\uD83D\uDE02")
      ))
    }

    scenario("multiple repeated words mentioned in a String with a single emoji") {
      Given("one emoji, repeated words")
      val s = "yay for yay emoji \uD83D\uDE04!"

      Then("a Map with every *distinct* (word, emoji) pair for every word in string, including emoji")
      assert(TwitterWordUsage.mapWordsToEmoji(s) === Map[String, Emoji](
        "yay"           -> Emoji("\uD83D\uDE04"),
        "for"           -> Emoji("\uD83D\uDE04"),
        "emoji"         -> Emoji("\uD83D\uDE04"),
        "\uD83D\uDE04!" -> Emoji("\uD83D\uDE04")
      ))
    }
  }
}
