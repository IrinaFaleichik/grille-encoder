package irka.grilleEncoder
package application.api.auth.identity

import application.api.*
import application.api.auth.identity.{EmailIdentity, Identity, UsernameIdentity}

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

trait Identity(using Seal):
  def validate: Either[String, Identity]

object Identity:

  import UsernameIdentity.*
  import EmailIdentity.*

  def fromCredential(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)

