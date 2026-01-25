package irka.grilleEncoder
package application.api

import application.api.*
import application.api.auth.{AuthService, AuthUserDto}
import core.repository.{AuthRepository, UserRepository}
import domain.errors.InvalidJson
import infrastructure.db.repository.auth.AuthRepositoryByUsername
import infrastructure.logging.LoggingExtensions.logErrorWithoutTrace
import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}

import zio.ZIO
import zio.http.*
import zio.json.DecoderOps

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
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val signUp: Route[AuthService, Response] = (
    Method.POST / "account" / "signup" -> handler: (request: Request) =>
      request.body.asString
        .flatMap(parseCredentials)
        .flatMap(AuthService.create)
        .map: _ =>
          Response.text("Welcome, ")
        //todo redirect to greet or settings route
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(anyError)
