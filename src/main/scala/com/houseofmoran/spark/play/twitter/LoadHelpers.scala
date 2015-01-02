package com.houseofmoran.spark.play.twitter

import java.io.{FilenameFilter, File}

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SchemaRDD, Row, SQLContext}

class ParquetLoadHelper(sqlContext: SQLContext) {
  def parquetFiles(dirName: String) : SchemaRDD = {
    return parquetFiles(dirName, """.+\.parquet""")
  }

  def parquetFiles(dirName: String, pattern: String) : SchemaRDD = {
    val filter = new FilenameFilter() {
      override def accept(dir: File, name: String): Boolean = {
        return name.matches(pattern)
      }
    }

    val allRDDFileNames =
      for(file <- new File(dirName).listFiles(filter))
        yield s"$dirName/${file.getName()}"

    return sqlContext.parquetFile(allRDDFileNames.mkString(","))
  }
}

object LoadHelpers {
  implicit def parquetLoadHelper(sqlContext: SQLContext): ParquetLoadHelper = {
    new ParquetLoadHelper(sqlContext)
  }
}
