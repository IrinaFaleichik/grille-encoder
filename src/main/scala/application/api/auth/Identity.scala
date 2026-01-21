package irka.grilleEncoder
package application.api.auth

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

sealed trait Identity

final case class UsernameIdentity(username: String, password: String) extends Identity

final case class EmailIdentity(email: String, password: String) extends Identity

object Identity:
  val MinUsernameLength: Int = 3
  val MinPasswordLength: Int = 6
  val MaxUsernameLength: Int = 20
  val MaxPasswordLength: Int = 20

  private val EmailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r
  private val UsernameRegex = "^[a-zA-Z0-9._-]+".r

  // Username validation function
  private def validateUsername(username: String): Either[String, String] =
    if (username.length < MinUsernameLength)
      Left(s"Username must be at least $MinUsernameLength characters long")
    else if (!UsernameRegex.matches(username))
      Left(s"Username cannot contain spaces or forbidden symbols")
    else
      Right(username)

  // Email validation function  
  private def validateEmail(email: String): Either[String, String] =
    if EmailRegex.matches(email) then Right(email)
    else Left("Invalid email format")

  // Password validation function
  private def validatePassword(password: String): Either[String, String] =
    if (password.length < MinPasswordLength)
      Left(s"Password must be at least $MinPasswordLength characters long")
    else
      Right(password)

  // Encoders
  implicit val usernameIdentityEncoder: JsonEncoder[UsernameIdentity] = DeriveJsonEncoder.gen[UsernameIdentity]
  implicit val emailIdentityEncoder: JsonEncoder[EmailIdentity] = DeriveJsonEncoder.gen[EmailIdentity]

  // Decoders with validation
  implicit val usernameIdentityDecoder: JsonDecoder[UsernameIdentity] =
    DeriveJsonDecoder.gen[UsernameIdentity].mapOrFail: identity =>
      for
        validUsername <- validateUsername(identity.username)
        validPassword <- validatePassword(identity.password)
      yield UsernameIdentity(validUsername, validPassword)

  implicit val emailIdentityDecoder: JsonDecoder[EmailIdentity] =
    DeriveJsonDecoder.gen[EmailIdentity].mapOrFail: identity =>
      for
        validEmail <- validateEmail(identity.email)
        validPassword <- validatePassword(identity.password)
      yield EmailIdentity(validEmail, validPassword)

  def fromUsername(username: String, password: String): Either[String, Identity] =
    for {
      validUsername <- validateUsername(username)
      validPassword <- validatePassword(password)
    } yield UsernameIdentity(validUsername, validPassword)

  def fromEmail(email: String, password: String): Either[String, Identity] =
    for {
      validEmail <- validateEmail(email)
      validPassword <- validatePassword(password)
    } yield EmailIdentity(validEmail, validPassword)

