package irka.grilleEncoder
package application.api.auth.identity

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class UsernameIdentity(username: String, password: String) extends Identity:
  def validate: Either[String, UsernameIdentity] =
    for
      validUsername <- validateUsername(username)
      validPassword <- validatePassword(password)
    yield UsernameIdentity(username = validUsername, password = validPassword)

object UsernameIdentity:
  implicit val encoder: JsonEncoder[UsernameIdentity] = DeriveJsonEncoder.gen[UsernameIdentity]
  implicit val decoder: JsonDecoder[UsernameIdentity] =
    DeriveJsonDecoder.gen[UsernameIdentity].mapOrFail(_.validate)
