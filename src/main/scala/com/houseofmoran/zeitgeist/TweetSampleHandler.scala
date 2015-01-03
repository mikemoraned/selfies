package com.houseofmoran.zeitgeist

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.json4s.JValue
import org.json4s.native.JsonMethods._

class TweetSampleHandler(contentMap: Map[String, {def toJSON() : JValue}]) extends AbstractHandler {
  val targetToContentMap = contentMap.map{ case (name, value) => (s"/api/$name", value) }

  override def handle(target: String, baseRequest: Request,
                       request: HttpServletRequest, response: HttpServletResponse): Unit =
   {
     targetToContentMap.get(target) match {
      case Some(content) => {
        response.setContentType("application/json; charset=utf-8")
        response.setStatus(HttpServletResponse.SC_OK)
        response.getWriter().print(pretty(render(content.toJSON())))

        baseRequest.setHandled(true);
      }
      case None => {}
    }
   }
 }
