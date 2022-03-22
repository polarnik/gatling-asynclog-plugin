package qaload.gatling.asynclogplugin.action

import java.text.SimpleDateFormat
import java.util.TimeZone

import io.gatling.commons.stats.{OK, Status}
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.Predef.Session
import io.gatling.core.action._
import io.gatling.core.session.Expression
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import qaload.gatling.asynclogplugin.request.AsynclogAttributes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LogAction(
                      attributes: AsynclogAttributes,
                      statsEngine: StatsEngine,
                      next: Action
                    ) extends Action with NameGen {

  override def name: String = genName("AsynclogRequest")


  def resolve[A](optionalExpression: Option[Expression[A]], session: Session, default: A): A =
    optionalExpression match {
      case Some(expression) => expression(session) match {
        case Success(value) => value
        case Failure(msg) => throw new RuntimeException(msg)
      }
      case None => default
    }

  def resolve[A](optionalExpression: Option[Expression[A]], session: Session): A =
    optionalExpression match {
      case Some(expression) => expression(session) match {
        case Success(value) => value
        case Failure(msg) => throw new RuntimeException(msg)
      }
      case None => throw new RuntimeException("Agrument is None")
    }

  def tryResolve[A](optionalExpression: Option[Expression[A]], session: Session):Option[A] =
    optionalExpression match {
      case Some(expression) => expression(session) match {
        case Success(value) => Some(value)
        case Failure(msg) => throw new RuntimeException(msg)
      }
      case None => None
    }

  def resolve[A](expression: Expression[A], session: Session): A =
    expression(session) match {
      case Success(value) => value
      case Failure(msg) => throw new RuntimeException(msg)
    }

  private def getTimeStamp(stringTime: String, format: String, timeZone: TimeZone = TimeZone.getTimeZone("GTM")): Long = {
    val sdf = new SimpleDateFormat(format)
    sdf.setTimeZone(timeZone)
    sdf.parse(stringTime).getTime
  }

  def getTime(
               session: Session,
               timeStamp: Option[Expression[Long]],
               timeStampDate: Option[Expression[java.util.Date]],
               timestampString: Option[Expression[String]],
               timestampStringFormat: Option[Expression[String]]
             ): scala.Long =
  {
    timeStamp match {
      case Some(timeStampExpression) => timeStampExpression(session) match {
        case Success(resolvedTimeStamp) => resolvedTimeStamp
        case Failure(msg) => throw new RuntimeException(msg)
      }
      case None => timeStampDate match {
        case Some(timeStampDateExpression) => timeStampDateExpression(session) match {
          case Success(resolvedTimeStampDate) => resolvedTimeStampDate.getTime
          case Failure(msg) => throw new RuntimeException(msg)
        }
        case None => (timestampString, timestampStringFormat) match {
          case (Some(timestampStringExpresstion), Some(timestampStringFormatExpression)) => {
            (timestampStringExpresstion(session), timestampStringFormatExpression(session)) match {
              case (Success(resolvedTimestampString), Success(resolvedTimestampStringFormat)) =>
                getTimeStamp(resolvedTimestampString, resolvedTimestampStringFormat)
              case (Failure(msg), Success(resolvedTimestampStringFormat)) =>
                throw new RuntimeException(msg)
              case (Success(resolvedTimestampString), Failure(msg)) =>
                throw new RuntimeException(msg)
              case (Failure(msg), Failure(msg2)) =>
                throw new RuntimeException(msg + " " + msg2)
            }
          }
          case (Some(timestampStringExpresstion), None) =>
            throw new RuntimeException("1")
          case (None, Some(timestampStringExpresstion)) =>
            throw new RuntimeException("2")
          case (None, None) =>
            throw new RuntimeException("3")
        }
      }
    }
  }

  override def execute(session: Session): Unit = {

    val resolvedAttributes: Future[ResolvedAttributes] = Future {
      val resolvedRequestName = resolve(attributes.requestName, session)
      val resolvedStartTimestamp = getTime(
        session,
        attributes.startTimestamp,
        attributes.startTimestampDate,
        attributes.startTimestampString,
        attributes.startTimestampStringFormat
      )
      val resolvedEndTimestamp = getTime(
        session,
        attributes.endTimestamp,
        attributes.endTimestampDate,
        attributes.endTimestampString,
        attributes.endTimestampStringFormat
      )
      val resolvedStatus = resolve(attributes.status, session, OK)

      val resolvedResponseCode = tryResolve(attributes.responseCode, session)

      val resolvedMessage = tryResolve(attributes.message, session)

      ResolvedAttributes(
        resolvedRequestName,
        resolvedStartTimestamp,
        resolvedEndTimestamp,
        resolvedStatus,
        resolvedResponseCode,
        resolvedMessage
      )
    }

    resolvedAttributes onComplete {
      case scala.util.Success(attr) => {
        println(attr)
        next ! {
          statsEngine.logResponse(
            session.scenario,
            session.groups,
            attr.requestName,
            attr.startTimestamp,
            attr.endTimestamp,
            attr.status,
            attr.responseCode,
            attr.message
          )
          session
        }
      }
      case scala.util.Failure(t) => {
        next ! {
          statsEngine.logCrash(session.scenario, session.groups, name, t.getMessage)
          session.markAsFailed
        }
      }
    }
  }
}