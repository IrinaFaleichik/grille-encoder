package irka.grilleEncoder
package application

import domain.errors.InvalidJson
import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}
import application.api.auth.identity.*

import application.api.auth.Role
import domain.model.{User, UserId}
import zio.ZIO
import zio.http.{Request, Response, Status}
import zio.json.{DecoderOps, EncoderOps, JsonDecoder}

import java.sql.SQLException
import scala.reflect.{ClassTag, classTag}

package object api:
  def anyError: PartialFunction[Throwable, Response] =
    e => Response.internalServerError(s"DB error: $e.getMessage")

  def jsonParsingError: PartialFunction[Throwable, Response] =
    case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)

  extension (pf: PartialFunction[Throwable, Response])
    def +(other: PartialFunction[Throwable, Response]): PartialFunction[Throwable, Response] =
      pf.orElse(other)

  private[api] def parseCredentials(request: Request): ZIO[Any, Throwable, Identity] =
    parseRequestBody[EmailIdentity](request).orElse:
      parseRequestBody[UsernameIdentity](request)

  private[api] def dbErrors: PartialFunction[Throwable, Response] =
    case e: SQLException => Response.internalServerError(e.getMessage)

  private[api] def parseRequestBody[DecodedType: JsonDecoder : ClassTag]
  (request: Request): ZIO[Any, Throwable, DecodedType] =
    request.body.asString
      .flatMap(json =>
        json.fromJson[DecodedType] match
          case Left(err) => ZIO.fail(InvalidJson(err)(formatExampleFor[DecodedType]))
          case Right(resultType) => ZIO.succeed(resultType)
      )

  private def formatExampleFor[T: ClassTag]: String =
    val tpe = classTag[T].runtimeClass
    if (tpe == classOf[UserId]) new UserId("example-id").toJson
    else if (tpe == classOf[Role]) Role.User.toJson
    else if (tpe == classOf[User]) User("example-id", "example-name").toJson
    else "{ }" // Default example
