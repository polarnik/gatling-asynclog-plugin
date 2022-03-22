package qaload.gatling.asynclogplugin

import io.gatling.core.session.Expression
import qaload.gatling.asynclogplugin.action.{LogActionBuilder}
import qaload.gatling.asynclogplugin.request.AsynclogAttributes

trait AsynclogDsl {

  def asynclog(requestName: Expression[String]) = LogActionBuilder(AsynclogAttributes.init()).requestName(requestName)

  val asynclog = LogActionBuilder(AsynclogAttributes.init())
}