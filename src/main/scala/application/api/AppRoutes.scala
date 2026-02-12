package irka.grilleEncoder
package application.api

import infrastructure.db
import infrastructure.db.repository.user.UserRepositoryDefault
import application.api.Auth.basicAuthWithUserContext
import application.api.auth.AuthService
import application.api.auth.dto.AuthUserDto

import application.api.auth.password.HashingUtils
import zio.*
import zio.http.*

object AppRoutes {

  val routes: Routes[UserRepositoryDefault & AuthService & HashingUtils, Response] =
    Routes(greetRoute) @@ Middleware.debug ++ test ++ UserRoutes.routes
      ++ AdminRoutes.routes ++ AuthRoutes.routes @@ Middleware.debug

  private lazy val test: Routes[AuthService, Response] = Routes(
    Method.GET / "test" -> handler:
      Response.text("Welcome to my service!") // todo add sandbox middleware
  )

  private lazy val greetRoute: Route[AuthService & HashingUtils, Response] =
    Method.POST / "account" / "me" -> handler: (_: Request) =>
      ZIO.serviceWith[AuthUserDto](i =>
        Response.text(s"Welcome ${i.username}!"),
      )
    @@ basicAuthWithUserContext

}
