package irka.grilleEncoder
package infrastructure.http

import domain.model.User
import infrastructure.db.repository.user.UserRepositoryDefault

import zio.json.*
import zio.ZIO
import zio.http.{Method, Request, Response, Route, Routes, handler}

object UserRoutes {
  val routes: Routes[UserRepositoryDefault, Throwable] = Routes(users, createUser, updateUser)

  lazy val users: Route[UserRepositoryDefault, Throwable] =// todo change to Database obj
      Method.GET / "users" -> handler {
        UserRepositoryDefault.get
          .map { users =>
            Response.text(users.toJson)
          }
      }
  lazy val createUser: Route[UserRepositoryDefault, Throwable] =
    Method.POST / "user" / "create" -> handler { (req: Request) =>
      req.body.asString.flatMap { json =>
        json.fromJson[User] match {
          case Left(err) =>
            // parsing failed: return 400
            ZIO.succeed(Response.text(s"Invalid JSON: $err"))
          case Right(user) =>
            // parsing succeeded
            UserRepositoryDefault.create(user)
              .flatMap(count => ZIO.succeed(Response.text(s"Created user: ${user.toJson}")))
        }
      }
    }
  lazy val updateUser: Route[UserRepositoryDefault, Throwable] =// todo change to Database obj
      Method.POST / "user" / "update" -> handler { (req: Request) =>
        req.body.asString.flatMap { json =>
          json.fromJson[User] match {
            case Left(err) =>
              // parsing failed: return 400
              ZIO.succeed(Response.text(s"Invalid JSON: $err"))
            case Right(user) =>
              // parsing succeeded
              UserRepositoryDefault.update(user)
                .flatMap(count => ZIO.succeed(Response.text(s"Updated user: ${user.toJson}")))
          }
        }
      }
}
