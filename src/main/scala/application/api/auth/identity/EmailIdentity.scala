package irka.grilleEncoder
package application.api.auth.identity

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class EmailIdentity(email: String, password: String) extends Identity:
  def validate: Either[String, EmailIdentity] =
    for
      validEmail <- validateEmail(email)
      validPassword <- validatePassword(password)
    yield EmailIdentity(email = email, password = validPassword)


object EmailIdentity:
  implicit val encoder: JsonEncoder[EmailIdentity] = DeriveJsonEncoder.gen[EmailIdentity]
  implicit val decoder: JsonDecoder[EmailIdentity] =
    DeriveJsonDecoder.gen[EmailIdentity].mapOrFail(_.validate)
