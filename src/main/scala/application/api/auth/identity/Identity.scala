package irka.grilleEncoder
package application.api.auth.identity

import application.api.*

trait Identity(using Seal):
  def validate: Either[String, Identity]

object Identity:

  import UsernameIdentity.*
  import EmailIdentity.*

  def fromCredential(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)

