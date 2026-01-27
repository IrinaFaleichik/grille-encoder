package irka.grilleEncoder
package application.api

import domain.model.{User, UserId}
import core.repository.UserRepository
import infrastructure.logging.LoggingExtensions.*

import zio.ZIO
import zio.http.*
import zio.json.*

/**
 * User management REST API endpoints
 * Provides CRUD operations for users
 */
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
    Method.POST / "user" / "create" -> handler: (request: Request) =>
      for
        _ <- ZIO.logInfo("Requested route create user")
        result <- parseRequestBody[User](request)
          .flatMap: user =>
            UserRepository.create(user)
              .map(count => Response.text(s"Created user: ${user.toJson}"))
          .logErrorWithoutTrace(_.getMessage)
      yield result
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val updateUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "update" -> handler: (request: Request) =>
      for
        _ <- ZIO.logInfo("Requested route update user")
        result <- parseRequestBody[User](request)
          .flatMap: user =>
            UserRepository.update(user)
              .map(count => Response.text(s"Updated user: ${user.toJson}"))
          .logErrorWithoutTrace(_.getMessage)
      yield result
    ).handleError(jsonParsingError + dbErrors + anyError)

  lazy val deleteUser: Route[UserRepository, Response] = (
    Method.POST / "user" / "delete" -> handler: (request: Request) =>
      for
        _ <- ZIO.logInfo("Requested route delete user")
        result <- parseRequestBody[UserId](request)
          .flatMap: userId =>
            UserRepository.delete(userId)
              .map(returnValue => Response.text(s"Deleted user: ${userId.toJson}"))
          .logErrorWithoutTrace(_.getMessage)
      yield result
    ).handleError(jsonParsingError + dbErrors + anyError)

}
