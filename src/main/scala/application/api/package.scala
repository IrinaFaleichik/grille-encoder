package irka.grilleEncoder
package application

import domain.errors.InvalidJson
import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}
import application.api.auth.identity.*
import application.api.auth.identity.Identity.*

import zio.ZIO
import zio.http.{Response, Status}
import zio.json.DecoderOps

import java.sql.SQLException

package object api:
  def anyError: PartialFunction[Throwable, Response] =
    e => Response.internalServerError(s"DB error: $e.getMessage")

  def jsonParsingError: PartialFunction[Throwable, Response] =
    case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)

  extension (pf: PartialFunction[Throwable, Response])
    def +(other: PartialFunction[Throwable, Response]): PartialFunction[Throwable, Response] =
      pf.orElse(other)

  def parseCredentials(json: String): ZIO[Any, Throwable, Identity] =
    json.fromJson[EmailIdentity] match
      case Left(err) => json.fromJson[UsernameIdentity] match
        case Left(err) => ZIO.fail(InvalidJson(err))
        case Right(identity) => ZIO.succeed(identity)
      case Right(identity) => ZIO.succeed(identity)

  private def dbErrors: PartialFunction[Throwable, Response] =
    case e: SQLException => Response.internalServerError(e.getMessage)
