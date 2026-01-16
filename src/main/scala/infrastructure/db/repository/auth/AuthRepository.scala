package irka.grilleEncoder
package infrastructure.db.repository.auth


import domain.model.UserId
import application.api.auth.AuthUserDto
import zio.{ZIO, ZLayer}
import zio.Config.Secret

// Interface for auth operations
trait AuthRepository {
  def authenticate(username: String, password: String): ZIO[Any, Throwable, AuthUserDto]

  def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]]

  def create(username: String, password: String, email: Option[String]): ZIO[Any, Throwable, AuthUserDto]
  // Other methods
}

// Companion with accessors
object AuthRepository {
  def authenticate(username: String, password: String): ZIO[AuthRepository, Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthRepository](_.authenticate(username, password))

  // Other accessors
}
