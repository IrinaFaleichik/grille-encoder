package irka.grilleEncoder
package application.api

import infrastructure.db
import infrastructure.db.repository.user.UserRepositoryDefault

import application.api.Auth.basicAuthWithUserContext
import application.api.auth.identity.UsernameIdentity
import application.api.auth.{AuthService, AuthUserDto}
import zio.*
import zio.http.*

object AppRoutes {

  //todo implement authentification, basic and through cookie
  val routes: Routes[UserRepositoryDefault & AuthService, Response] =
    greetRoute ++ test ++ UserRoutes.routes

  private lazy val test: Routes[AuthService, Response] = Routes(
    Method.GET / "test" -> handler:
      Response.text("Welcome to my service!") // todo add sandbox middleware
  )

  lazy val greetRoute: Routes[AuthService, Response] = Routes(
    Method.POST / "account" / "me" -> handler: (_: Request) =>
      ZIO.serviceWith[AuthUserDto](i =>
        Response.text(s"Welcome ${i.username}!"),
      )
    @@ basicAuthWithUserContext,
  ) @@ Middleware.debug

}
