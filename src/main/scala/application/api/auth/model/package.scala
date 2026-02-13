package irka.grilleEncoder
package application.api.auth

import domain.model.UserId
import application.api.auth.dto.Role
import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}
import application.api.auth.password.{HashedPassword, HashingUtils}

import infrastructure.db.entities.TableEntity
import zio.ZIO

package object model:

  case class AuthUser(
                       id: UserId,
                       username: String,
                       password: HashedPassword,
                       email: Option[String] = None,
                       role: Role = Role.User
                     ):

    def toTableEntity: TableEntity.AuthUser =
      TableEntity.AuthUser(
        id = this.id,
        username = this.username,
        passwordHash = this.password.hash, // Note: we need to expose this for db storage
        role = this.role.ordinal,
        email = this.email
      )

  object AuthUser:
    def generateId: UserId = java.util.UUID.randomUUID().toString

    def randomUsername(email: String): String =
      val prefix = email.split("@")(0)
      val randomSuffix = java.util.UUID.randomUUID().toString.take(8)
      s"$prefix-$randomSuffix"

    def createFromIdentity(identity: Identity): ZIO[HashingUtils, Throwable, AuthUser] =
      for {
        hashedPassword <- HashingUtils.fromPlainText(identity.password)
        user = identity match {
          case email: EmailIdentity =>
            AuthUser(
              id = generateId,
              username = randomUsername(email.email),
              password = hashedPassword,
              email = Some(email.email)
            )
          case username: UsernameIdentity =>
            AuthUser(
              id = generateId,
              username = username.username,
              password = hashedPassword,
              email = None
            )
        }
      } yield user

