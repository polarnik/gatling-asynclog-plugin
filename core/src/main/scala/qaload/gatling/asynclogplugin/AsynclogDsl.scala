package ru.raiffeisen.asynclog

import io.gatling.core.session.Expression
import ru.raiffeisen.asynclog.request.AsynclogDslBuilderBase
import io.gatling.core.config.GatlingConfiguration
import qaload.gatling.asynclogplugin.action.{LogActionBuilder}
import qaload.gatling.asynclogplugin.request.AsynclogAttributes

trait AsynclogDsl {

  def asynclog(requestName: Expression[String]) = LogActionBuilder(AsynclogAttributes.init()).requestName(requestName)
}