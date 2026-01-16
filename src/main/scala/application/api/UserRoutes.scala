package irka.grilleEncoder
package application.api

import domain.errors.InvalidJson
import domain.model.User
import infrastructure.db.repository.user.UserRepository
import zio.ZIO
import zio.http.*
import zio.json.*

import java.sql.SQLException

object UserRoutes {
  val routes: Routes[UserRepository, Response] = Routes(getUsers, createUser, updateUser, deleteUser)

  lazy val getUsers: Route[UserRepository, Response] = (
    Method.GET / "users" -> handler {
      UserRepository.get
        .map { users =>
          Response.text(users.toJson)
        }
    }).handleError(dbErrors + anyError)

  lazy val createUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "create" -> handler { (req: Request) =>
      req.body.asString
        .flatMap(parse)
        .flatMap { user =>
          // parsing succeeded
          UserRepository.create(user)
            .map(count => Response.text(s"Created user: ${user.toJson}"))
        }
    }).handleError(jsonParsingError + dbErrors + anyError)

  lazy val updateUser: Route[UserRepository, Response] = {
    (
      Method.POST / "user" / "update" -> handler { (req: Request) =>
        req.body.asString
          .flatMap(parse)
          .flatMap { user =>
            // parsing succeeded
            UserRepository.update(user)
              .map(count => Response.text(s"Updated user: ${user.toJson}"))
          }
      }).handleError(jsonParsingError + dbErrors + anyError)
  }

  lazy val deleteUser: Route[UserRepository, Response] = {
    (
      Method.POST / "user" / "delete" -> handler { (req: Request) =>
        req.body.asString
          .flatMap(parse)
          .flatMap { user =>
            UserRepository.delete(user)
              .map(returnValue => Response.text(s"Deleted user: ${user.toJson}"))
          }
      }).handleError(jsonParsingError + dbErrors + anyError)
  }

  private def parse(json: String): ZIO[Any, Throwable, User] =
    json.fromJson[User] match {
      case Left(err) => ZIO.fail(InvalidJson(err))
      case Right(user) => ZIO.succeed(user)
    }

  private def dbErrors: PartialFunction[Throwable, Response] =
    case e: SQLException => Response.internalServerError(e.getMessage)

  private def jsonParsingError: PartialFunction[Throwable, Response] =
    case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)

  private def anyError: PartialFunction[Throwable, Response] =
    e => Response.internalServerError(s"DB error: $e.getMessage")


  extension (pf: PartialFunction[Throwable, Response]) {
    def +(other: PartialFunction[Throwable, Response]): PartialFunction[Throwable, Response] =
      pf.orElse(other)
  }
}
