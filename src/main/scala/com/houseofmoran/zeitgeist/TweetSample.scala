package com.houseofmoran.zeitgeist

import java.io.PrintWriter
import org.apache.spark.rdd.RDD
import twitter4j.Status

case class TweetSample() {
   var possibleSample : Option[Seq[Status]] = None

   def summarise(writer: PrintWriter): Unit = {
     for (sample <- possibleSample) {
       val entries = for(status <- sample if status.getGeoLocation() != null)
         yield "{ \"id\" : \"" + status.getId + "\", \"location\" :" +
           " { \"lat\": " + status.getGeoLocation.getLatitude + ", \"lon\": " + status.getGeoLocation.getLongitude + "} }"

       writer.println(entries.mkString(",\n"))
     }
   }

   def newWindow(window: RDD[(Long, Status)]) : Unit = {
     possibleSample = Some(window.takeSample(true, 10).map{ case (_, status) => status })
   }
 }
