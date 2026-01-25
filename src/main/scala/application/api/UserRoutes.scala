package irka.grilleEncoder
package application.api

import domain.errors.InvalidJson
import domain.model.{User, UserId}
import core.repository.UserRepository
import infrastructure.logging.LoggingExtensions.*

import zio.ZIO
import zio.http.*
import zio.json.*

//todo rename, it is not a User, it's more a UserDB CRUD methods API
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
          .flatMap(parseId)
          .flatMap: userId =>
            UserRepository.delete(userId)
              .map(returnValue => Response.text(s"Deleted user: ${userId.toJson}"))
          .logErrorWithoutTrace(_.getMessage)
      yield result
    ).handleError(jsonParsingError + dbErrors + anyError)

  private def parse(json: String): ZIO[Any, Throwable, User] =
    json.fromJson[User] match
      case Left(err) => ZIO.fail(InvalidJson(err))
      case Right(user) => ZIO.succeed(user)

  private def parseId(json: String): ZIO[Any, Throwable, UserId] =
    json.fromJson[UserId] match
      case Left(err) => ZIO.fail(InvalidJson(err))
      case Right(userId) => ZIO.succeed(userId)

}
