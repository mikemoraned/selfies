package com.houseofmoran.spark.play.twitter

import com.houseofmoran.spark.play.twitter.LoadHelpers._
import org.apache.spark._
import org.apache.spark.sql.SQLContext

object AnalyseTwitterApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AnalyseTwitter").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sqlSc = new SQLContext(sc)

    val wordCounts = sqlSc.parquetFiles("wordcounts")

    for(row <- wordCounts.takeSample(false, 10)) {
      println(row.mkString(","))
    }
  }
}
