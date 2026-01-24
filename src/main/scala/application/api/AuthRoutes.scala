package irka.grilleEncoder
package application.api

import application.api.*
import application.api.UserRoutes.dbErrors
import application.api.auth.{AuthService, AuthUserDto, EmailIdentity, Identity, UsernameIdentity}
import core.repository.{AuthRepository, UserRepository}
import domain.errors.InvalidJson
import infrastructure.db.repository.auth.AuthRepositoryByUsername
import infrastructure.logging.LoggingExtensions.logErrorWithoutTrace

import zio.ZIO
import zio.http.*
import zio.json.DecoderOps
import zio.prelude.data.Optional.AllValuesAreNullable

object AuthRoutes:
  val routes: Routes[AuthService, Response] = Routes(login)

  lazy val login: Route[AuthService, Response] = (
    Method.POST / "account" / "login" -> handler: (request: Request) =>
      request.body.asString
        .flatMap(parseCredentials)
        .flatMap(AuthService.authenticate)
        .map: _ =>
          Response.text("Login succeed! Enjoy your stay!")
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(anyError)

  //  lazy val signUp: Route[AuthService, Response] = (
  //    Method.POST / "account" / "signup" -> handler: (request: Request) =>
  //      request.body.asString
  //        .flatMap(parseCredentials)
  //        .flatMap(AuthService.)
  //        .map: _ =>
  //          Response.text("Login succeed! Enjoy your stay!")
  //        .logErrorWithoutTrace(_.getMessage)
  //    ).handleError(anyError)

  // todo json parsing is pretty common for a lot of routes, maybe
  // move it to package object and use a logging for easier error handling?
  private def parseCredentials(json: String): ZIO[Any, Throwable, Identity] =
    json.fromJson[EmailIdentity] match
      case Left(err) => json.fromJson[UsernameIdentity] match
        case Left(err) => ZIO.fail(InvalidJson(err))
        case Right(identity) => ZIO.succeed(identity)
      case Right(identity) => ZIO.succeed(identity)
