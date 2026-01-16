package irka.grilleEncoder
package infrastructure.db.repository.auth

import domain.model.UserId
import application.api.auth.AuthUserDto
import infrastructure.db.entities.DBContext
import zio.{ZIO, ZLayer}
import io.getquill.*
import infrastructure.db.entities.TableEntity

import java.sql.SQLException

final class AuthRepositoryDefault(ctx: DBContext) extends AuthRepository {

  import ctx.*

  // Password hashing utils
  private def hashPassword(password: String): String = ???

  private def verifyPassword(password: String, hash: String): Boolean = ???

  override def authenticate(username: String, password: String): ZIO[Any, Throwable, AuthUserDto] =
    for {
      userOpt <- ctx.run(
        quote {
          query[TableEntity.AuthUser].filter(u => u.username == lift(username))
        }
      ).map(_.headOption)
      user <- userOpt match {
        case Some(record) if verifyPassword(password, record.passwordHash) =>
          ZIO.succeed(record.toDto)
        case _ =>
          ZIO.fail(new Exception("Invalid credentials"))
      }
    } yield user

  // Other implementations

  override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

  override def create(username: UserId, password: UserId, email: Option[UserId]): ZIO[Any, Throwable, AuthUserDto] = ???
}

object AuthRepositoryDefault {
  lazy val live: ZLayer[DBContext, Nothing, AuthRepositoryDefault] =
    ZLayer.fromFunction(new AuthRepositoryDefault(_))
}
