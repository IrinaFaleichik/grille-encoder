package irka.grilleEncoder
package application.api.auth

import core.repository.AuthRepository

import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}
import zio.{ZIO, ZLayer}

// Strategy pattern for authenticating users
trait AuthService:
  def authenticate(identity: Identity): ZIO[Any, Throwable, AuthUserDto]

  def create(identity: Identity): ZIO[Any, Throwable, AuthUserDto]

object AuthService:
  def authenticate(identity: Identity): ZIO[AuthService, Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthService](_.authenticate(identity))

  def create(identity: Identity): ZIO[AuthService, Throwable, AuthUserDto] =
    ZIO.serviceWithZIO[AuthService](_.create(identity))

  private final class DatabaseAuthService(
                                           usernameRepo: AuthRepository[UsernameIdentity],
                                           emailRepo: AuthRepository[EmailIdentity]
                                         ) extends AuthService:
    override def authenticate(identity: Identity): ZIO[Any, Throwable, AuthUserDto] =
      identity match
        case i: UsernameIdentity => usernameRepo.authenticate(i)
        case i: EmailIdentity => ZIO.fail(new Exception("Email authentication is not implemented yet"))

    override def create(identity: Identity): ZIO[Any, Throwable, AuthUserDto] = ???

  // Live layer that combines repositories
  val live: ZLayer[
    AuthRepository[UsernameIdentity]
      & AuthRepository[EmailIdentity]
    ,
    Nothing,
    AuthService
  ] =
    ZLayer {
      for {
        usernameRepo <- ZIO.service[AuthRepository[UsernameIdentity]]
        emailRepo <- ZIO.service[AuthRepository[EmailIdentity]]
      } yield new DatabaseAuthService(usernameRepo, emailRepo)
    }
