package qaload.gatling.asynclogplugin.action

import io.gatling.commons.stats.Status

final case class ResolvedAttributes(
                                     requestName: String,
                                     startTimestamp: Long,
                                     endTimestamp: Long,
                                     status: Status,
                                     responseCode: Option[String],
                                     message: Option[String]
                                   )
