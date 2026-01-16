package irka.grilleEncoder
package application.api

import application.api.auth.{AuthUser, AuthUserDto}
import infrastructure.db.repository.auth.{AuthRepository, AuthRepositoryDefault}
import zio.Config.Secret

class Auth {

  import zio._
  import zio.http._

  // Basic auth! Пиздато! To protect routes with it, but it's not in the cookie, it's in header
  val basicAuthWithUserContext: HandlerAspect[AuthRepository, AuthUserDto] = {
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
      ZIO.serviceWithZIO[AuthRepository] { authRepository =>
        request.header(Header.Authorization) match {
          case Some(Header.Authorization.Basic(username, password)) =>
            //users - db request
            //password insecure ?
            val authResult = authRepository.authenticate(username, password.toString()) //todo password secure?
            authResult.map(auth => (request, auth)).mapError(_ =>
              Response
                .unauthorized("Invalid username or password")
                .addHeaders(Headers(Header.WWWAuthenticate.Basic(realm = Some("Protected API"))))
            )
          case _ =>
            ZIO.fail(
              Response
                .unauthorized("Authentication required")
                .addHeaders(Headers(Header.WWWAuthenticate.Basic(realm = Some("Protected API")))),
            )
        }
      }
    })
  }
}
