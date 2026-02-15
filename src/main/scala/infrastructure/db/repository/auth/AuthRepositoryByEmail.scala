package irka.grilleEncoder
package infrastructure.db.repository.auth

import core.repository.AuthRepository
import domain.model.UserId

import application.api.auth.model
import infrastructure.db.entities.{DBContext, TableEntity}
//import infrastructure.db.entities.{DBContext, TableEntity}

import io.getquill.*
import application.api.auth.identity.{EmailIdentity, Identity}
import application.api.auth.dto.AuthUserDto
import application.api.auth.password.{HashedPassword, HashingUtils}
//import infrastructure.db.entities.TableEntity

//import application.api.auth.model
import zio.{ZIO, ZLayer}


final class AuthRepositoryByEmail(ctx: DBContext) extends AuthRepository[EmailIdentity]:

  import ctx.*

  // Password hashing utils
  private def hashPassword(password: String): ZIO[HashingUtils, Throwable, HashedPassword] = HashingUtils.fromPlainText(password)

  private def verifyPassword(dbPassword: String, hash: HashedPassword): Boolean = hash.verifyHashed(dbPassword)

  override def authenticate(identity: EmailIdentity): ZIO[HashingUtils, Throwable, AuthUserDto] =
    for
      _ <- ZIO.logInfo(s"authenticating identity: ${identity.email}")
      userFromIdentity <- model.AuthUser.createFromIdentity(identity) //todo add error type: sys env variables for Secret is not configured, error
      userOpt <- ctx.run(
        quote:
          query[TableEntity.AuthUser].filterByKeys(Map("email" -> identity.email))
      ).map(_.headOption)
      _ <- ZIO.logInfo(s"Successfully found record: ${identity.email}")
      userFromIdentity <- model.AuthUser.createFromIdentity(identity)
      user <- userOpt match
        case Some(record) if userFromIdentity.password.verifyHashed(record.passwordHash) =>
          ZIO.succeed(record.toDto)
        case _ =>
          ZIO.fail(new Exception("Invalid credentials, username or password is invalid"))
    yield user

  // Other implementations

  override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] =
    ZIO.fail(new Exception("Email authentication is not implemented yet"))

  override def create(identity: EmailIdentity): ZIO[HashingUtils, Throwable, List[(Long, AuthUserDto)]] =
    for
      _ <- ZIO.logInfo(s"Creating users: ${identity.email}")
      authUser <- model.AuthUser.createFromIdentity(identity)
      tableEntity = List(authUser.toTableEntity)
      _ <- ZIO.logInfo(s"Creating users: ${identity.email}")
      result <- ctx.run:
        quote:
          liftQuery(tableEntity).foreach(v => query[TableEntity.AuthUser].insertValue(v))
    yield result.zip(tableEntity.map(_.toDto))

//  override def create(identity: EmailIdentity): ZIO[Any, Throwable, AuthUserDto] =
//    ZIO.fail(new Exception("Email authentication is not implemented yet"))
// todo 1) go to db, 2) check if user exists
//  3) if user exists, give an error or redirect to login
//  4) ) if user doesn't exist, create user and return user


object AuthRepositoryByEmail:
  lazy val live: ZLayer[DBContext, Nothing, AuthRepositoryByEmail] =
    ZLayer.fromFunction(new AuthRepositoryByEmail(_))

