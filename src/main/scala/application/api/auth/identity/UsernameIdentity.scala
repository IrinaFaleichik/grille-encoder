package irka.grilleEncoder
package application.api.auth.identity

import application.api.auth.Role
import infrastructure.db.entities.TableEntity.AuthUser
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class UsernameIdentity(username: String, password: String) extends Identity:
  def validate: Either[String, UsernameIdentity] =
    for
      validUsername <- validateUsername(username)
      validPassword <- validatePassword(password)
    yield UsernameIdentity(username = validUsername, password = validPassword)

  def createFromIdentity: AuthUser =
    AuthUser(
      id = AuthUser.generateId,
      username = username,
      passwordHash = password, //todo generate password hash
      role = Role.User.ordinal,
      email = None,
    )

object UsernameIdentity:
  implicit val encoder: JsonEncoder[UsernameIdentity] = DeriveJsonEncoder.gen[UsernameIdentity]
  implicit val decoder: JsonDecoder[UsernameIdentity] =
    DeriveJsonDecoder.gen[UsernameIdentity].mapOrFail(_.validate)
