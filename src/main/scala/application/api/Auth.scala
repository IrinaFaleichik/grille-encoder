package irka.grilleEncoder
package application.api

import application.api.auth.{AuthService, AuthUser, AuthUserDto}
import application.api.auth.identity.Identity

import zio.Config.Secret

object Auth:

  import zio._
  import zio.http._

  // Basic auth! To protect routes with it, but it's not in the cookie, it's in the header for now
  val basicAuthWithUserContext: HandlerAspect[AuthService, AuthUserDto] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request]: request =>
      ZIO.serviceWithZIO[AuthService]: authService =>
        request.header(Header.Authorization) match
          case Some(Header.Authorization.Basic(emailOrUsername, password)) =>
            for
              _ <- ZIO.logInfo(s"Authenticating user: $emailOrUsername")
              authResult <- ZIO.fromEither(
                  Identity.fromCredential(emailOrUsername, password.toString())
                )
                .flatMap(identity => authService.authenticate(identity))
                .mapError(errorMsg => Response
                  .unauthorized(s"Invalid credentials: $errorMsg")
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
