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
    // List of all the routes
    greetRoute ++ UserRoutes.routes
  // Handle all unhandled errors

  // A route that matches GET requests to /greet
  // It doesn't require any service from the ZIO environment
  // so the first type parameter is Any
  // All its errors are handled so the second type parameter is Nothing
  //todo add test route? and delete this route

  lazy val greetRoute: Routes[AuthService, Response] = Routes(
    Method.POST / "account" / "me" -> handler: (_: Request) =>
      ZIO.serviceWith[AuthUserDto](i =>
        Response.text(s"Welcome ${i.username}!"),
      )
    @@ basicAuthWithUserContext,
  ) @@ Middleware.debug

}
