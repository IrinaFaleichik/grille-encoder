package irka.grilleEncoder
package infrastructure.db.repository.auth

import application.api.auth.{AuthUserDto, PasswordHash}
import core.repository.AuthRepository
import domain.model.UserId
import infrastructure.db.entities.{DBContext, TableEntity}

import io.getquill.*
import application.api.auth.identity.{EmailIdentity, Identity}

import zio.{ZIO, ZLayer}


final class AuthRepositoryByEmail(ctx: DBContext) extends AuthRepository[EmailIdentity]:

  import ctx.*

  // Password hashing utils
  private def hashPassword(password: String): PasswordHash = PasswordHash.fromPlainText(password)

  private def verifyPassword(dbPassword: String, hash: PasswordHash): Boolean = hash.verify(dbPassword)

  override def authenticate(identity: EmailIdentity): ZIO[Any, Throwable, AuthUserDto] =
    ZIO.fail(new Exception("Email authentication is not implemented yet"))
    for
      userOpt <- ctx.run(
        quote:
          query[TableEntity.AuthUser].filter(
            u => u.email.contains(lift(identity.email))
          )
      ).map(_.headOption)
      user <- userOpt match
        case Some(record) if verifyPassword(record.passwordHash, hashPassword(identity.password)) =>
          ZIO.succeed(record.toDto)
        case _ =>
          ZIO.fail(new Exception("Invalid credentials"))
    yield user

  // Other implementations

  override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] =
    ZIO.fail(new Exception("Email authentication is not implemented yet"))

  override def create(identity: EmailIdentity): ZIO[Any, Throwable, AuthUserDto] =
    ZIO.fail(new Exception("Email authentication is not implemented yet"))
  // todo 1) go to db, 2) check if user exists
  //  3) if user exists, give an error or redirect to login
  //  4) ) if user doesn't exist, create user and return user


object AuthRepositoryByEmail:
  lazy val live: ZLayer[DBContext, Nothing, AuthRepositoryByEmail] =
    ZLayer.fromFunction(new AuthRepositoryByEmail(_))

