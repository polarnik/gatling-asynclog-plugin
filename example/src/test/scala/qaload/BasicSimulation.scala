package qaload


import java.text.SimpleDateFormat
import java.util.Calendar

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import qaload.gatling.asynclogplugin.Predef._


class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec {
      session =>
        val now = Calendar.getInstance().getTime()
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val start = dateFormat.format(now)
      session
          .set("startTime", start)
    }
    .exec(
      http("/ (GET)").get("/")
    )
    .exec {
      session =>
        val now = Calendar.getInstance().getTime()
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val stop = dateFormat.format(now)
        session
          .set("stopTime", stop)
    }
    .exec {
      session =>
        session
    }
    .exec(asynclog("Generate req 1")
      .startTimestamp("${startTime}", "yyyy-MM-dd HH:mm:ss")
        .endTimestamp("${stopTime}", "yyyy-MM-dd HH:mm:ss")
        .status(io.gatling.commons.stats.OK)
        .responseCode("200")
    )
    .exec(asynclog
      .requestName("Generate req 2")
      .startTimestamp("${startTime}", "yyyy-MM-dd HH:mm:ss")
      .endTimestamp("${stopTime}", "yyyy-MM-dd HH:mm:ss")
      .status(io.gatling.commons.stats.OK)
      .responseCode("200")
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
