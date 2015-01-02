package com.houseofmoran.zeitgeist

import java.io.PrintWriter
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import com.houseofmoran.spark.twitter.TwitterStreamSource
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._
import org.eclipse.jetty.server.handler.{AbstractHandler, HandlerList, ResourceHandler}
import org.eclipse.jetty.server.{Handler, Request, Server}
import twitter4j.Status

import scala.util.Properties


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

class TweetSampleHandler(sample: TweetSample) extends AbstractHandler {
  override def handle(target: String, baseRequest: Request,
                      request: HttpServletRequest, response: HttpServletResponse): Unit =
  {
    response.setContentType("application/json; charset=utf-8")
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter().println("[")
    sample.summarise(response.getWriter)
    response.getWriter().println("]")
    baseRequest.setHandled(true);
  }
}

object TwitterVisApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterVisApp").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val windowLength = Seconds(10)
    implicit val ssc = new StreamingContext(sc, windowLength)

    val twitterStream =
      if (args.length > 0) {
        println("Reading auth from file")
        TwitterStreamSource.streamFromAuthIn(args(0))
      }
      else {
        println("Reading auth from env")
        TwitterStreamSource.streamFromEnv()
      }

    val sample = new TweetSample

    val sampleHandler = new TweetSampleHandler(sample)

    val stream = twitterStream.
      filter(status => status.getGeoLocation() != null).
      map(status => (status.getId, status))

    stream.foreachRDD( rdd => sample.newWindow(rdd) )

    val server = new Server(Properties.envOrElse("PORT", "8080").toInt)

    val resources = new ResourceHandler()
    resources.setWelcomeFiles(Array[String]("index.html"))
    resources.setResourceBase("./src/main/resources")

    val handlers = new HandlerList()
    handlers.setHandlers(Array[Handler]( resources, sampleHandler ))

    server.setHandler(handlers)
    server.start()

    ssc.start()
    ssc.awaitTermination()
  }
}
