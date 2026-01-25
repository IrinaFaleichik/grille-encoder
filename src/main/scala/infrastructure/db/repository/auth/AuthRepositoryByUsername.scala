package irka.grilleEncoder
package infrastructure.db.repository.auth

import domain.model.UserId
import application.api.auth.{AuthUserDto, PasswordHash}
import infrastructure.db.entities.DBContext

import zio.{ZIO, ZLayer}
import io.getquill.*
import infrastructure.db.entities.TableEntity
import core.repository.AuthRepository
import application.api.auth.identity.{Identity, UsernameIdentity}

import java.sql.SQLException

final class AuthRepositoryByUsername(ctx: DBContext) extends AuthRepository[UsernameIdentity] {

  import ctx.*

  // Password hashing utils
  private def hashPassword(password: String): PasswordHash = PasswordHash.fromPlainText(password)

  private def verifyPassword(dbPassword: String, hash: PasswordHash): Boolean = hash.verify(dbPassword)

  override def authenticate(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] =
    for
      userOpt <- ctx.run(
        quote:
          query[TableEntity.AuthUser].filter(
            u => u.username == lift(identity.username)
          )
      ).map(_.headOption)
      user <- userOpt match
        case Some(record) if verifyPassword(record.passwordHash, hashPassword(identity.password)) =>
          ZIO.succeed(record.toDto)
        case _ =>
          ZIO.fail(new Exception("Invalid credentials"))
    yield user

  // Other implementations

  override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

  override def create(identity: Identity): ZIO[Any, Throwable, AuthUserDto] = ???
  // todo 1) go to db, 2) check if user exists
  //  3) if user exists, give an error or redirect to login
  //  4) ) if user doesn't exist, create user and return user

}

object AuthRepositoryByUsername {
  lazy val live: ZLayer[DBContext, Nothing, AuthRepositoryByUsername] =
    ZLayer.fromFunction(new AuthRepositoryByUsername(_))
}
