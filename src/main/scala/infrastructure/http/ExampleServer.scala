package irka.grilleEncoder.infrastructure.http

import zio.*
import zio.http.*
import irka.grilleEncoder.domain.model.*
import irka.grilleEncoder.infrastructure
import zio.json.EncoderOps

object ExampleServer extends ZIOAppDefault {

  // A route that matches GET requests to /greet
  // It doesn't require any service from the ZIO environment
  // so the first type parameter is Any
  // All its errors are handled so the second type parameter is Nothing
  val greetRoute: Route[Any, Nothing] =
    // The whole Method.GET / "greet" is a RoutePattern
    Method.GET / "greet" ->
      // The handler is a function that takes a Request and returns a Response
      handler { (req: Request) =>
        val name = req.queryOrElse[String]("name", "World")
        Response.text(s"Hello $name!")
      }

  // A route that matches POST requests to /echo
  // It doesn't require any service from the ZIO environment
  // It is an unhandled route so the second type parameter is something other than Nothing
  val echoRoute: Route[Any, Throwable] =
    Method.POST / "echo" -> handler { (req: Request) =>
      req.body.asString.map(Response.text(_))
    }

  val studentsRoutes =
    Routes(
      Method.GET / "users" -> handler {
        infrastructure.db.Api.getUsers.map { user =>
          Response.text(user.toJson)
        }
      }
    )

  // The Routes that don't require any service from the ZIO environment,
  // so the first type parameter is Any.
  // All the errors are handled by turning them into a Response.
  val routes: Routes[Any, Response] =
    // List of all the routes
    Routes(greetRoute, echoRoute)
      // Handle all unhandled errors
      .++(studentsRoutes)
      .handleError(e => Response.internalServerError(e.getMessage))

  // Serving the routes using the default server layer on port 8080
  def run = for {
    _ <- Server.serve(routes).provide(Server.defaultWithPort(8083))
  } yield ()
}
