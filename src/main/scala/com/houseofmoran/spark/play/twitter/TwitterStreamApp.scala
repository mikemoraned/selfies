package com.houseofmoran.spark.play.twitter

import com.github.nscala_time.time.Imports._
import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

object TwitterStreamApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterStream").setMaster("local[*]")
    val sc = new SparkContext(conf)
    implicit val ssc = new StreamingContext(sc, Seconds(1))
    ssc.checkpoint("checkpoint")
    val sqlSc = new SQLContext(sc)
    import sqlSc.createSchemaRDD

    val twitterStream = TwitterStreamSource.streamFromAuthIn(args(0))

    val windowLength = Seconds(60)

    val wordStream = twitterStream.flatMap(status => status.getText().split(" "))
    val wordCountStream = wordStream.countByValueAndWindow(windowLength, windowLength)

    wordCountStream.foreachRDD( wordCountRDD => {
      val now = DateTime.now.toDateTimeISO

      wordCountRDD.map( wordCount => (now.toInstant().millis, windowLength.milliseconds, wordCount._1, wordCount._2))
                  .saveAsParquetFile(s"wordcounts/start${ISODateTimeFormat.dateTime().print(now)}_window${windowLength.milliseconds}ms.parquet")
    });

    ssc.start()
    ssc.awaitTermination()
  }
}
