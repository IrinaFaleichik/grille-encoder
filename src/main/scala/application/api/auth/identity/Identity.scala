package irka.grilleEncoder
package application.api.auth.identity

import application.api.*
import application.api.auth.model.AuthUser
import application.api.auth.password.HashingUtils
import zio.ZIO

trait Identity(using Seal):
  val password: String

  def validate: Either[String, Identity]

object Identity:

  import UsernameIdentity.*
  import EmailIdentity.*

  def fromCredential(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)

