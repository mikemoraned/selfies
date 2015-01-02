package com.houseofmoran.spark.play.twitter

import com.github.nscala_time.time.Imports._
import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

object TwitterEmojiStreamApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterEmojiStream").setMaster("local[*]")
    val sc = new SparkContext(conf)
    implicit val ssc = new StreamingContext(sc, Seconds(1))
    ssc.checkpoint("checkpoint")
    val sqlSc = new SQLContext(sc)
    import sqlSc.createSchemaRDD

    val twitterStream = TwitterStreamSource.streamFromAuthIn(args(0))

    val windowLength = Seconds(60)

    val emojiStream = twitterStream.flatMap(status => TwitterWordUsage.mapWordsToEmoji(status.getText))
    val emojiCountStream = emojiStream.countByValueAndWindow(windowLength, windowLength)

    emojiCountStream.foreachRDD( emojiCountRDD => {
//      emojiCountRDD.collect().foreach(println)

      val now = DateTime.now.toDateTimeISO

      emojiCountRDD.
        map( emojiCount => (now.toInstant().millis, windowLength.milliseconds, emojiCount._1, emojiCount._2)).
        saveAsParquetFile(s"emojiusage/start${ISODateTimeFormat.dateTime().print(now)}_window${windowLength.milliseconds}ms.parquet")
    });

    ssc.start()
    ssc.awaitTermination()
  }
}
