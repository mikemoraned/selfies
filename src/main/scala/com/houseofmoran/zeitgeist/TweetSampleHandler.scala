package com.houseofmoran.zeitgeist

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class TweetSampleHandler(sample: TweetSample) extends AbstractHandler {
   override def handle(target: String, baseRequest: Request,
                       request: HttpServletRequest, response: HttpServletResponse): Unit =
   {
     if (target == "/api/sample") {
       response.setContentType("application/json; charset=utf-8")
       response.setStatus(HttpServletResponse.SC_OK)
       response.getWriter().print(pretty(render(sample.toJSON())))

       baseRequest.setHandled(true);
     }
   }
 }
