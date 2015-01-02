package com.houseofmoran.zeitgeist

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

class TweetSampleHandler(sample: TweetSample) extends AbstractHandler {
   override def handle(target: String, baseRequest: Request,
                       request: HttpServletRequest, response: HttpServletResponse): Unit =
   {
     if (target == "/api/sample") {
       response.setContentType("application/json; charset=utf-8")
       response.setStatus(HttpServletResponse.SC_OK)
       response.getWriter().println("[")
       sample.summarise(response.getWriter)
       response.getWriter().println("]")
       baseRequest.setHandled(true);
     }
   }
 }
