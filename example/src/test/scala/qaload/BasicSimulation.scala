package qaload


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
    .exec(asynclog("Generate PDF Report (prepare report complete)")
      .startTimestamp("2019-11-25 00:19:00", "yyyy-MM-dd HH:mm:ss")
        .endTimestamp("2019-11-25 00:29:00", "yyyy-MM-dd HH:mm:ss")
        .status(io.gatling.commons.stats.OK)
        .responseCode("200")
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
