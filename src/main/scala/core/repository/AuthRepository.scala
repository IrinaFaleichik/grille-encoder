package irka.grilleEncoder
package core.repository

import application.api.auth.AuthUserDto
import domain.model.UserId

import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}
import zio.Config.Secret
import zio.ZIO

// Interface for auth operations
trait AuthRepository[I <: Identity]:
  def authenticate(identity: I): ZIO[Any, Throwable, AuthUserDto]

  def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]]

  def create(identity: I): ZIO[Any, Throwable, AuthUserDto]

// Companion with accessors
object AuthRepository:
  def authenticate(identity: Identity): ZIO[AuthRepository[Identity], Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthRepository[Identity]](_.authenticate(identity))

  def findById(id: UserId): ZIO[AuthRepository[_], Throwable, Option[AuthUserDto]] =
    ZIO.serviceWithZIO[AuthRepository[_]](_.findById(id))

  def create(identity: Identity):
  ZIO[AuthRepository[Identity], Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthRepository[Identity]](_.create(identity))

