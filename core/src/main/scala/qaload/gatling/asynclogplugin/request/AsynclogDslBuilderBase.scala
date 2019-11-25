package ru.raiffeisen.asynclog.request

import io.gatling.core.session.Expression
import qaload.gatling.asynclogplugin.request.AsynclogAttributes
import ru.raiffeisen.asynclog.request._

case class AsynclogDslBuilderBase(transactionID: Expression[Any]) {
  def log(requestName: Expression[String]) = LogDslBuilder(attributes = AsynclogAttributes.init()).requestName(requestName)

}
