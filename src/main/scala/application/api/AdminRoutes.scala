package irka.grilleEncoder
package application.api

import application.api.auth.{AuthService, AuthUserDto, Role}

import zio.http.Route
import application.api.*
import infrastructure.logging.LoggingExtensions.logErrorWithoutTrace
import domain.model.UserId

import zio.*
import zio.http.*
import application.api.Auth.adminAuth

import zio.json.EncoderOps

object AdminRoutes {
  val routes: Routes[AuthService, Response] = Routes(createUserWithRole) @@ Middleware.debug //, deleteUser) @@ Middleware.debug

  private lazy val createUserWithRole: Route[AuthService, Response] =
    Method.POST / "role" / "change" -> handler { (request: Request) =>
      for {
        _ <- ZIO.logInfo("Entering route /role/change")
        authResult <- parseRequestBody[UserId](request)
          .zip(parseRequestBody[Role](request))
          .flatMap { case (userId: UserId, role: Role) =>
            AuthService.changeRole(userId, role)
              .map(_ => Response.text(s"Role successfully changed for user $userId"))
          }
          .logErrorWithoutTrace(_.getMessage)
        authUser <- ZIO.service[AuthUserDto]
      } yield Response.text(s"User ${authUser.username} made a privileged action: change user role")
    }.mapError(dbErrors + jsonParsingError + anyError) @@ Auth.adminAuth

  lazy val deleteUser: Route[AuthService, Response] = ???

}
