package irka.grilleEncoder
package infrastructure.http

import domain.model.User
import infrastructure.db.repository.user.{UserRepository, UserRepositoryDefault}

import zio.json.*
import zio.ZIO
import zio.http.{Method, Request, Response, Route, Routes, handler}

import java.sql.SQLException

object UserRoutes {
  val routes: Routes[UserRepository, Response] = Routes(getUsers, createUser, updateUser)

  lazy val getUsers: Route[UserRepository, Response] = (
    Method.GET / "users" -> handler {
      UserRepository.get
        .map { users =>
          Response.text(users.toJson)
        }
    }).handleError {
    _ match
      case e: SQLException => Response.internalServerError(e.getMessage)
      case e => Response.internalServerError(s"DB error: $e.getMessage")
  }

  lazy val createUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "create" -> handler { (req: Request) =>
      req.body.asString.flatMap { json =>
        json.fromJson[User] match {
          case Left(err) =>
            // parsing failed: return 400
            ZIO.succeed(Response.text(s"Invalid JSON: $err"))
          case Right(user) =>
            // parsing succeeded
            UserRepository.create(user)
              .flatMap(count => ZIO.succeed(Response.text(s"Created user: ${user.toJson}")))
        }
      }
    }).handleError {
    _ match
      case e: SQLException => Response.internalServerError(e.getMessage)
      case e => Response.internalServerError(s"DB error: $e.getMessage")
  }
  lazy val updateUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "update" -> handler { (req: Request) =>
      req.body.asString.flatMap { json =>
        json.fromJson[User] match {
          case Left(err) =>
            // parsing failed: return 400
            ZIO.succeed(Response.text(s"Invalid JSON: $err"))
          case Right(user) =>
            // parsing succeeded
            UserRepository.update(user)
              .flatMap(count => ZIO.succeed(Response.text(s"Updated user: ${user.toJson}")))
        }
      }
    }).handleError {
    _ match
      case e: SQLException => Response.internalServerError(e.getMessage)
      case e => Response.internalServerError(s"DB error: $e.getMessage")
  }
}
