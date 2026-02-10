package irka.grilleEncoder
package application.api.auth.identity

import application.api.auth.PasswordHash
import application.api.auth.dto.Role
import application.api.auth.model.AuthUser
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class EmailIdentity(email: String, password: String) extends Identity:
  def validate: Either[String, EmailIdentity] =
    for
      validEmail <- validateEmail(email)
      validPassword <- validatePassword(password)
    yield EmailIdentity(email = email, password = validPassword)

  def toNewUser: AuthUser =
    AuthUser(
      id = AuthUser.generateId,
      username = AuthUser.randomUsername(email),
      password = PasswordHash.fromPlainText(password), //todo generate password hash
      role = Role.User,
      email = Some(email),
    )

object EmailIdentity:
  implicit val encoder: JsonEncoder[EmailIdentity] = DeriveJsonEncoder.gen[EmailIdentity]
  implicit val decoder: JsonDecoder[EmailIdentity] =
    DeriveJsonDecoder.gen[EmailIdentity].mapOrFail(_.validate)
