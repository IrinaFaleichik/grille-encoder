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
  val routes: Routes[UserRepository, Response] = Routes(getUsers, createUser, updateUser)

  lazy val getUsers: Route[UserRepository, Response] = (
    Method.GET / "users" -> handler {
      UserRepository.get
        .map { users =>
          Response.text(users.toJson)
        }
    }).handleError {
      case e: SQLException => Response.internalServerError(e.getMessage)
      case e => Response.internalServerError(s"DB error: $e.getMessage")
  }

  lazy val createUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "create" -> handler { (req: Request) =>
      req.body.asString
        .flatMap(parse)
        .flatMap { user =>
            // parsing succeeded
            UserRepository.create(user)
              .map(count => Response.text(s"Created user: ${user.toJson}"))
        }
    }).handleError {
      case e: SQLException => Response.internalServerError(e.getMessage)
      case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)
      case e => Response.internalServerError(s"DB error: $e.getMessage")
  }

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
      }).handleError {
        case e: SQLException => Response.internalServerError(e.getMessage)
        case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)
        case e => Response.internalServerError(s"DB error: $e.getMessage")
    }
  }

  // todo implement delete userRepository, then uncomment
//  lazy val deleteUser: Route[UserRepository, Response] = {
//    (
//      Method.DELETE / "user" / "delete" -> handler { (req: Request) =>
//        req.body.asString
//          .flatMap(parse)
//          .flatMap { user =>
//              // parsing succeeded
//              UserRepository.delete(user)
//                .map(returnValue => Response.text(s"Deleted user: ${user.toJson}"))
//        }
//      }).handleError {
//      case e: SQLException => Response.internalServerError(e.getMessage)
//      case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)
//      case e => Response.internalServerError(s"DB error: $e.getMessage")
//    }
//  }

  private def parse(json: String): ZIO[Any, Throwable, User] =
    json.fromJson[User] match {
      case Left(err) => ZIO.fail(InvalidJson(err))
      case Right(user) => ZIO.succeed(user)
  }

//  //todo rename
//  def applyDbFunction(usr: User, userRep: User => ZIO[UserRepository, Throwable, List[Long]]) = {
//    userRep(usr)
//  }
}
