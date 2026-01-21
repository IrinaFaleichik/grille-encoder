package irka.grilleEncoder
package core.repository

import application.api.auth.{AuthUserDto, Identity}
import domain.model.UserId

import zio.Config.Secret
import zio.{ZIO, ZLayer}

// Interface for auth operations
trait AuthRepository[I <: Identity] {
  def authenticate(identity: I): ZIO[Any, Throwable, AuthUserDto]
  // todo 1) go to db, 2) check password, 3) return user

  def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]]

  def create(username: String, password: String, email: Option[String]): ZIO[Any, Throwable, AuthUserDto]
  // Other methods
}

// Companion with accessors
object AuthRepository {
  def authenticate(identity: Identity): ZIO[AuthRepository[Identity], Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthRepository[Identity]](_.authenticate(identity))

  // Other accessors
}
