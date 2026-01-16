package irka.grilleEncoder
package application.api

import infrastructure.db
import infrastructure.db.repository.user.UserRepositoryDefault
import zio.*
import zio.http.*

import java.sql.SQLException

object AppRoutes {


  // The Routes that don't require any service from the ZIO environment,
  // so the first type parameter is Any.
  // All the errors are handled by turning them into a Response.
  //todo implement authentification, basic and through cookie
  val routes: Routes[UserRepositoryDefault, Response] =
    // List of all the routes
    greetRoute ++ UserRoutes.routes
  // Handle all unhandled errors

  // A route that matches GET requests to /greet
  // It doesn't require any service from the ZIO environment
  // so the first type parameter is Any
  // All its errors are handled so the second type parameter is Nothing
  //todo add test route? and delete this route
  lazy val greetRoute: Routes[Any, Response] =
    // The whole Method.GET / "greet" is a RoutePattern
    Routes(Method.GET / "greet" ->
      // The handler is a function that takes a Request and returns a Response
      handler { (req: Request) =>
        val name = req.queryOrElse[String]("name", "World")
        Response.text(s"Hello $name!")
      }).handleError(e => Response.internalServerError(s"Internal server error"))

}
