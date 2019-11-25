package qaload.gatling.asynclogplugin.request

import io.gatling.commons.stats.Status
import io.gatling.core.session.Expression
import io.gatling.core.session.ExpressionSuccessWrapper
import io.gatling.core.session.ExpressionFailureWrapper

case class AsynclogAttributes(
                               requestName: Expression[String],

                               startTimestamp: Option[Expression[Long]],
                               startTimestampString: Option[Expression[String]],
                               startTimestampStringFormat: Option[Expression[String]],

                               endTimestamp: Option[Expression[Long]],
                               endTimestampString: Option[Expression[String]],
                               endTimestampStringFormat: Option[Expression[String]],

                               status: Option[Expression[Status]],
                               responseCode: Option[Expression[String]],
                               message: Option[Expression[String]],
                               maxDuration: Option[Expression[Long]]
                             )

object AsynclogAttributes {

  def init(): AsynclogAttributes = {
    val initAttributes = AsynclogAttributes(
      requestName = "".expressionSuccess,

      startTimestamp = None,
      startTimestampString = None,
      startTimestampStringFormat = None,

      endTimestamp = None,
      endTimestampString = None,
      endTimestampStringFormat = None,

      status = None,
      responseCode = None,
      message = None,
      maxDuration = None
    )
    initAttributes
  }

  def initStartTimestamp() : AsynclogAttributes = {

    val startAttributes = AsynclogAttributes(
      requestName = "".expressionSuccess,

      startTimestamp = Some(System.currentTimeMillis.expressionSuccess),
      startTimestampString = None,
      startTimestampStringFormat = None,

      endTimestamp = None,
      endTimestampString = None,
      endTimestampStringFormat = None,

      status = None,
      responseCode = None,
      message = None,
      maxDuration = None
    )
    startAttributes
  }
}