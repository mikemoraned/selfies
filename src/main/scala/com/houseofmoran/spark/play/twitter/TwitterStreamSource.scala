package com.houseofmoran.spark.play.twitter

import java.io.FileReader
import java.util.Properties

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils

import scala.collection.JavaConversions._

object TwitterStreamSource {
  def streamFromEnv()(implicit streamingContext: StreamingContext) = {
    val env = System.getenv()

    for (key <- env.keySet()
         if key.startsWith("twitter4j_oauth_")) {
      System.setProperty(key.replaceAllLiterally("_", "."), env(key))
    }

    TwitterUtils.createStream(streamingContext, None)
  }

  def streamFromAuthIn(propertiesFile: String)(implicit streamingContext: StreamingContext) = {
    val oauthProperties = new Properties()
    oauthProperties.load(new FileReader(propertiesFile))

    for (key <- oauthProperties.stringPropertyNames()
         if key.startsWith("twitter4j.oauth.")) {
      System.setProperty(key, oauthProperties.getProperty(key))
    }

    TwitterUtils.createStream(streamingContext, None)
  }
}
