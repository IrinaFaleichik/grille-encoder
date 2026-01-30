package irka.grilleEncoder
package application.api

import application.api.*
import application.api.auth.{AuthService, AuthUserDto}
import infrastructure.logging.LoggingExtensions.logErrorWithoutTrace
import application.api.auth.identity.Identity
import domain.model.User

import zio.ZIO
import zio.http.*
import zio.json.EncoderOps

object AuthRoutes:
  val routes: Routes[AuthService, Response] = Routes(login, signUp)

  lazy val login: Route[AuthService, Response] = (
    Method.POST / "account" / "login" -> handler: (request: Request) =>
      parseCredentials(request)
        .flatMap(AuthService.authenticate)
        .map: user =>
          Response.text(s"Login succeed! Enjoy your stay, ${user.username}!")
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val signUp: Route[AuthService, Response] =
    Method.POST / "account" / "signup" -> handler: (request: Request) =>
      for
        _ <- ZIO.logInfo("entering route: /account/signup")
        authUser <- ZIO.service[AuthUserDto]
      //          .flatMap(AuthService.create)
      //          .map: user =>
      //              Response.text(s"Welcome, ${user}!")
      //          .logErrorWithoutTrace(_.getMessage)
      yield Response.text(s"Signing up...")
    .mapError(dbErrors + jsonParsingError + anyError) @@ Auth.basicAuthWithUserContext @@ redirectToGreet

  lazy val redirectToGreet: HandlerAspect[AuthService, Any] =
    HandlerAspect.redirect(URL(Path("/account/me")))
