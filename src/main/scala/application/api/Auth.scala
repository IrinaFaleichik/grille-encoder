
package irka.grilleEncoder
package application.api

import application.api.auth.AuthService
import application.api.auth.identity.Identity
import application.api.auth.dto.AuthUserDto

import application.api.auth.password.HashingUtils

object Auth:

  import zio._
  import zio.http._

  /** Creates a basic auth handler aspect that extracts credentials from HTTP headers
   * and processes them using the provided authentication function */
  // todo redo to JWT? write token in a db (mb redis? for cache), and return it in the response
  private def basicAuthHandler(
                                authenticate: Identity => ZIO[AuthService & HashingUtils, Throwable, AuthUserDto]
                              ): HandlerAspect[AuthService & HashingUtils, AuthUserDto] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request]: request =>
      request.header(Header.Authorization) match
        case Some(Header.Authorization.Basic(emailOrUsername, password)) =>
          for
            _ <- ZIO.logInfo(s"Authenticating user: $emailOrUsername")
            authResult <- ZIO.fromEither(
                Identity.fromCredential(emailOrUsername, password.stringValue)
              )
              .flatMap(identity => authenticate(identity))
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

  /** Basic authentication handler for normal user access */
  val basicAuthWithUserContext: HandlerAspect[AuthService & HashingUtils, AuthUserDto] =
    basicAuthHandler(AuthService.authenticate)

  /** Basic authentication handler requiring admin privileges */
  val adminAuth: HandlerAspect[AuthService & HashingUtils, AuthUserDto] =
    basicAuthHandler(AuthService.authenticateAdmin)