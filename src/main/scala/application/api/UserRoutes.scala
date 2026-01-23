package irka.grilleEncoder
package application.api

import domain.errors.InvalidJson
import domain.model.User
import core.repository.UserRepository

import infrastructure.logging.LoggingExtensions._
import zio.ZIO
import zio.http.*
import zio.json.*

import java.sql.SQLException

object UserRoutes {
  val routes: Routes[UserRepository, Response] = Routes(getUsers, createUser, updateUser, deleteUser)

  lazy val getUsers: Route[UserRepository, Response] = (
    Method.GET / "users" -> handler:
      UserRepository.get
        .map: users =>
          Response.text(users.toJson)
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(dbErrors + anyError)

  lazy val createUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "create" -> handler: (req: Request) =>
      req.body.asString
        .flatMap(parse)
        .flatMap: user =>
          UserRepository.create(user)
            .map(count => Response.text(s"Created user: ${user.toJson}"))
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val updateUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "update" -> handler: (req: Request) =>
      req.body.asString
        .flatMap(parse)
        .flatMap: user =>
          UserRepository.update(user)
            .map(count => Response.text(s"Updated user: ${user.toJson}"))
        .logErrorWithoutTrace(_.getMessage)
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val deleteUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "delete" -> handler: (req: Request) =>
      for
        _ <- ZIO.logInfo("Requested route delete user")
        result <- req.body.asString
          .flatMap(parse)
          .flatMap: user =>
            UserRepository.delete(user)
              .map(returnValue => Response.text(s"Deleted user: ${user.toJson}"))
          .logErrorWithoutTrace(_.getMessage)
      yield result
    ).handleError(jsonParsingError + dbErrors + anyError)

  private def parse(json: String): ZIO[Any, Throwable, User] =
    json.fromJson[User] match
      case Left(err) => ZIO.fail(InvalidJson(err))
      case Right(user) => ZIO.succeed(user)

  private def dbErrors: PartialFunction[Throwable, Response] =
    case e: SQLException => Response.internalServerError(e.getMessage)

}
