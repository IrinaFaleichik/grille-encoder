package irka.grilleEncoder
package infrastructure.db.repository.auth

import domain.model.UserId
import infrastructure.db.entities.DBContext

import zio.{ZIO, ZLayer}
import io.getquill.*
import infrastructure.db.entities.TableEntity
import core.repository.AuthRepository
import application.api.auth.identity.{Identity, UsernameIdentity}
import infrastructure.db.entities.TableEntity.AuthUserEntity
import application.api.auth.dto.AuthUserDto
import application.api.auth.password.{HashedPassword, HashingUtils}

import application.api.auth.model.AuthUser

import java.sql.SQLException

final class AuthRepositoryByUsername(ctx: DBContext) extends AuthRepository[UsernameIdentity]:

  import ctx.*

  override def authenticate(identity: UsernameIdentity): ZIO[HashingUtils, Throwable, AuthUserDto] =
    for
      _ <- ZIO.logInfo(s"authenticating: ${identity.username}")
      userFromIdentity <- AuthUser.createFromIdentity(identity)
      userOpt <- ctx.run(
        quote:
          query[TableEntity.AuthUserEntity].filterByKeys(Map("username" -> identity.username))
      ).map(_.headOption)
      _ <- ZIO.logInfo(s"Successfully found record: ${identity.username}")
      user <- userOpt match
        case Some(record) if userFromIdentity.password.verifyHashed(record.passwordHash) =>
          ZIO.succeed(record.toDto)
        case _ =>
          ZIO.fail(new Exception("Invalid credentials, username or password is invalid"))
    yield user

  // Other implementations

  override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

  override def create(identity: UsernameIdentity): ZIO[HashingUtils, Throwable, List[(Long, AuthUserDto)]] =
    for
      _ <- ZIO.logInfo(s"Creating users: ${identity.username}")
      authUser <- AuthUser.createFromIdentity(identity)
      tableEntity = List(authUser.toTableEntity)
      result <- ctx.run:
        quote:
          liftQuery(tableEntity).foreach(v => query[AuthUserEntity].insertValue(v))
    yield result.zip(tableEntity.map(_.toDto))


object AuthRepositoryByUsername:
  lazy val live: ZLayer[DBContext, Nothing, AuthRepositoryByUsername] =
    ZLayer.fromFunction(AuthRepositoryByUsername.apply(_))

