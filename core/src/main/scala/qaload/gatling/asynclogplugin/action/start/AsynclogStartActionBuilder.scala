package qaload.gatling.asynclogplugin.action.start

import io.gatling.core.session.{Expression, _}
import qaload.gatling.asynclogplugin.request.AsynclogAttributes
import com.softwaremill.quicklens._

case class AsynclogStartActionBuilder(
  transactionID: Expression[Any],
  attributes: AsynclogAttributes
) {
  def label(requestName: Expression[String]) =
    this.modify(_.attributes.requestName).setTo(requestName)

  def maxDuration(maxDuration: Expression[Long]) =
    this.modify(_.attributes.maxDuration).setTo(Some(maxDuration))
}
