package irka.grilleEncoder
package application.api

import application.api.auth.{AuthUser, AuthUserDto, Identity}
import infrastructure.db.repository.auth.AuthRepositoryByUsername
import core.repository.AuthRepository
import zio.Config.Secret

class Auth:

  import zio._
  import zio.http._

  // Basic auth! To protect routes with it, but it's not in the cookie, it's in the header for now
  // todo to long, refactor
  // todo for now works only with UsernameIdentity!
  val basicAuthWithUserContext: HandlerAspect[AuthRepository[Identity], AuthUserDto] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request]: request =>
      ZIO.serviceWithZIO[AuthRepository[Identity]]: authRepository =>
        request.header(Header.Authorization) match
          case Some(Header.Authorization.Basic(emailOrUsername, password)) =>
            for
              //users - make a db request
              _ <- ZIO.logInfo(s"Authenticating user: $emailOrUsername")
              identityEither =
                Identity.fromEmail(emailOrUsername, password.toString())
                  .orElse(Identity.fromUsername(emailOrUsername, password.toString()))
              identity <- ZIO.fromEither(identityEither).mapError(errorMsg =>
                Response
                  .unauthorized(s"Invalid credentials: $errorMsg")
                  .addHeaders(Headers(Header.WWWAuthenticate.Basic(realm = Some("Protected API"))))
              )
              authResult <- authRepository.authenticate(identity).mapError(_ =>
                Response
                  .unauthorized("Invalid username or password")
                  .addHeaders(Headers(Header.WWWAuthenticate.Basic(realm = Some("Protected API"))))
              )
            yield (request, authResult)

          case _ =>
            ZIO.fail(
              Response
                .unauthorized("Authentication required")
                .addHeaders(Headers(Header.WWWAuthenticate.Basic(realm = Some("Protected API")))),
            )
    )

