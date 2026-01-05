package irka.grilleEncoder.infrastructure.http

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.*
import zio.http.*
import irka.grilleEncoder.domain.model.*
import irka.grilleEncoder.infrastructure
import irka.grilleEncoder.infrastructure.db
import irka.grilleEncoder.infrastructure.db.{SQLiteDatabase, entities}
import irka.grilleEncoder.infrastructure.db.repository.user.UserRepositoryDefault
import zio.json.EncoderOps
import zio.json._ // for .fromJson/.toJson
import zio.http.Body // for req.body.asString
import java.sql.SQLException
import javax.sql.DataSource

object AppRoutes {


  // The Routes that don't require any service from the ZIO environment,
  // so the first type parameter is Any.
  // All the errors are handled by turning them into a Response.
  //todo implement authentification, basic and through cookie
  val routes: Routes[UserRepositoryDefault, Response] =
    // List of all the routes
    Routes(greetRoute)
      .++(UserRoutes.userRoutes)
      // Handle all unhandled errors
      .handleError {_ match
        case e: SQLException => Response.internalServerError(e.getMessage)
        case e => Response.internalServerError(s"DB error: $e.getMessage")
      }

  // A route that matches GET requests to /greet
  // It doesn't require any service from the ZIO environment
  // so the first type parameter is Any
  // All its errors are handled so the second type parameter is Nothing
  lazy val greetRoute: Route[Any, Throwable] =
    // The whole Method.GET / "greet" is a RoutePattern
    Method.GET / "greet" ->
      // The handler is a function that takes a Request and returns a Response
      handler { (req: Request) =>
        val name = req.queryOrElse[String]("name", "World")
        Response.text(s"Hello $name!")
      }

}
