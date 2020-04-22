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
        val now = System.currentTimeMillis;
        val nowDate = Calendar.getInstance().getTime()
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz")
        val nowString = dateFormat.format(nowDate)
      session
          .set("start", now)
          .set("startDate", nowDate)
          .set("startDateString", nowString)
    }
    .exec(
      http("/?1 (GET)").get("/?1")
    )
    .exec(
      http("/?2 (GET)").get("/?2")
    )
    .exec(
      http("/?3 (GET)").get("/?3")
    )
    .exec {
      session =>
        val now = System.currentTimeMillis;
        val nowDate = Calendar.getInstance().getTime()
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz")
        val nowString = dateFormat.format(nowDate)
        session
          .set("stop", now)
          .set("stopDate", nowDate)
          .set("stopDateString", nowString)
    }
    .exec(asynclog
      .requestName("Generate req 1")
      .startTimestamp("${start}")
      .endTimestamp("${stop}")
    )
    .exec(asynclog
      .requestName("Generate req 2")
      .startTimestamp("${start}")
      .endTimestamp("${stop}")
      .status(io.gatling.commons.stats.KO)
      .responseCode("500")
      .message("500 Error Message")
    )
    .exec(asynclog
      .requestName("Generate req 3")
      .startDate("${startDate}")
      .endDate("${stopDate}")
    )
    .exec(asynclog
      .requestName("Generate req 4")
      .startTimestamp("${startDateString}", "yyyy-MM-dd HH:mm:ss.SSS zzz")
      .endTimestamp("${stopDateString}", "yyyy-MM-dd HH:mm:ss.SSS zzz")
      .status(io.gatling.commons.stats.OK)
      .responseCode("200")
      .message("startTimestamp(String, Format), endTimestamp(String, Format) [${startDateString}, ${stopDateString}]")
    )
    .exec(asynclog
      .requestName("Generate req 5")
      .startTimestamp("${start}")
      .endTimestamp("${stopDateString}", "yyyy-MM-dd HH:mm:ss.SSS zzz")
      .status(io.gatling.commons.stats.OK)
      .responseCode("201")
      .message("startTimestamp(System.currentTimeMillis), endTimestamp(String, Format) [${start}, ${stopDateString}]")
    )

  setUp(
    scn.inject(
      atOnceUsers(1),
      atOnceUsers(2),
      atOnceUsers(3),
      atOnceUsers(4)
    ).protocols(httpProtocol))
}
