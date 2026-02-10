package irka.grilleEncoder
package application.api.auth.identity

import application.api.auth.PasswordHash

import application.api.auth.dto.Role
import application.api.auth.model.AuthUser
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class UsernameIdentity(username: String, password: String) extends Identity:
  def validate: Either[String, UsernameIdentity] =
    for
      validUsername <- validateUsername(username)
      validPassword <- validatePassword(password)
    yield UsernameIdentity(username = validUsername, password = validPassword)

  def toNewUser: AuthUser =
    AuthUser(
      id = AuthUser.generateId,
      username = username,
      password = PasswordHash.fromPlainText(password), //todo generate password hash
      role = Role.User,
      email = None,
    )

object UsernameIdentity:
  implicit val encoder: JsonEncoder[UsernameIdentity] = DeriveJsonEncoder.gen[UsernameIdentity]
  implicit val decoder: JsonDecoder[UsernameIdentity] =
    DeriveJsonDecoder.gen[UsernameIdentity].mapOrFail(_.validate)
