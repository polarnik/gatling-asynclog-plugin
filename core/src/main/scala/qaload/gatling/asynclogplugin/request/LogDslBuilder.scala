package ru.raiffeisen.asynclog.request

import java.text.SimpleDateFormat
import java.util.TimeZone

import com.softwaremill.quicklens._
import io.gatling.commons.stats.Status
import io.gatling.core.session.{Expression, _}
import qaload.gatling.asynclogplugin.request.AsynclogAttributes


case class LogDslBuilder (attributes: AsynclogAttributes) {
  def requestName(requestName: Expression[String]): LogDslBuilder =
    this.modify(_.attributes.requestName).setTo(requestName)


  def startTimestamp(startTimestamp: Expression[Long]): LogDslBuilder =
    this.modify(_.attributes.startTimestamp).setTo(Some(startTimestamp))


  def startTimestamp(startTime: Expression[String], format: Expression[String]): LogDslBuilder =
    this
      .modify(_.attributes.startTimestampString).setTo(Some(startTime))
      .modify(_.attributes.startTimestampStringFormat).setTo(Some(format))

  def endTimestamp(endTimestamp: Expression[Long]): LogDslBuilder =
    this.modify(_.attributes.endTimestamp).setTo(Some(endTimestamp))

  def status(status: Status): LogDslBuilder =
    this.modify(_.attributes.status).setTo(Some(status.expressionSuccess))

  def status(status: Expression[Status]): LogDslBuilder =
    this.modify(_.attributes.status).setTo(Some(status))

  def responseCode(responseCode: Expression[String]): LogDslBuilder =
    this.modify(_.attributes.responseCode).setTo(Some(responseCode))

  def message(message: Expression[String]): LogDslBuilder =
    this.modify(_.attributes.message).setTo(Some(message))



}

