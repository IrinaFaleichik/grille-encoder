package irka.grilleEncoder
package application.api.auth

import domain.model.UserId

import application.api.auth.dto.Role
import infrastructure.db.entities.TableEntity.AuthUserEntity

package object model:

  case class AuthUser(
                       id: UserId,
                       username: String,
                       password: PasswordHash,
                       email: Option[String] = None,
                       role: Role = Role.User
                     ):

    def toTableEntity: AuthUserEntity =
      AuthUserEntity(
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

    def providePassword: String = ???

//    // Factory methods for creating new users
//    def fromIdentity():
//    AuthUser =
//  ...

