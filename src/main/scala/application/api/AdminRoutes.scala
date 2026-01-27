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

object AdminRoutes {
  val routes: Routes[AuthService, Response] = Routes(createUserWithRole) @@ Middleware.debug //, deleteUser) @@ Middleware.debug
  private lazy val createUserWithRole: Route[AuthService, Response] =
    Method.POST / "role" / "change" -> {
      handler: (request: Request, authService: AuthService) =>
        (
          for
            _ <- ZIO.logInfo("Entering route /role/change")
            authResult <- parseRequestBody[UserId](request)
              .zip(parseRequestBody[Role](request))
              .flatMap { case (userId: UserId, role: Role) =>
                AuthService.changeRole(userId, role)
                  .map(u => Response.text(s""))
              }
              .logErrorWithoutTrace(_.getMessage)
          yield authResult
          ) &> ZIO.serviceWith[AuthUserDto](i =>
          Response.text(s"User ${i.username} made a privileged action: change user role")
        )
    }.mapError(e => Response.text(s"Welcome!")) @@ Auth.adminAuth

  lazy val deleteUser: Route[AuthService, Response] = ???

}
