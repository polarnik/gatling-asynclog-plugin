package qaload.gatling.asynclogplugin.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import com.softwaremill.quicklens._
import io.gatling.commons.stats.Status
import io.gatling.core.session.{Expression, _}
import qaload.gatling.asynclogplugin.request.AsynclogAttributes

case class LogActionBuilder (attributes: AsynclogAttributes) extends ActionBuilder {

  def requestName(requestName: Expression[String]) =
    this.modify(_.attributes.requestName).setTo(requestName)


  def startTimestamp(startTimestamp: Expression[Long]) =
    this.modify(_.attributes.startTimestamp).setTo(Some(startTimestamp))

  def startDate(startDate: Expression[java.util.Date]) =
    this.modify(_.attributes.startTimestampDate).setTo(Some(startDate))

  def startTimestamp(startTime: Expression[String], format: Expression[String]) =
    this
      .modify(_.attributes.startTimestampString).setTo(Some(startTime))
      .modify(_.attributes.startTimestampStringFormat).setTo(Some(format))


  def endTimestamp(endTimestamp: Expression[Long]) =
    this.modify(_.attributes.endTimestamp).setTo(Some(endTimestamp))

  def endDate(endDate: Expression[java.util.Date]) =
    this.modify(_.attributes.endTimestampDate).setTo(Some(endDate))

  def endTimestamp(endTime: Expression[String], format: Expression[String]) =
    this
      .modify(_.attributes.endTimestampString).setTo(Some(endTime))
      .modify(_.attributes.endTimestampStringFormat).setTo(Some(format))


  def status(status: Status) =
    this.modify(_.attributes.status).setTo(Some(status.expressionSuccess))

  def status(status: Expression[Status]) =
    this.modify(_.attributes.status).setTo(Some(status))

  def responseCode(responseCode: Expression[String]) =
    this.modify(_.attributes.responseCode).setTo(Some(responseCode))

  def message(message: Expression[String]) =
    this.modify(_.attributes.message).setTo(Some(message))


  override def build(
                      ctx: ScenarioContext,
                      next: Action
                    ): Action = {

    LogAction(attributes, ctx.coreComponents.statsEngine, next)
  }

}
