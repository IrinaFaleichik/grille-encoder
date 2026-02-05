package irka.grilleEncoder
package application.api.auth.identity

import application.api.*

import infrastructure.db.entities.TableEntity.AuthUser

trait Identity(using Seal):
  val password: String

  def validate: Either[String, Identity]

  def createFromIdentity: AuthUser

object Identity:

  import UsernameIdentity.*
  import EmailIdentity.*

  def fromCredential(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)

