package irka.grilleEncoder
package application.api.auth

import application.api.*
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

sealed trait Identity:
  def validate: Either[String, Identity]

final case class UsernameIdentity(username: String, password: String) extends Identity:
  def validate: Either[String, UsernameIdentity] =
    for
      validUsername <- Identity.validateUsername(username)
      validPassword <- Identity.validatePassword(password)
    yield UsernameIdentity(username = validUsername, password = validPassword)

final case class EmailIdentity(email: String, password: String) extends Identity:
  def validate: Either[String, EmailIdentity] =
    for
      validEmail <- Identity.validateEmail(email)
      validPassword <- Identity.validatePassword(password)
    yield EmailIdentity(email = email, password = validPassword)

object Identity:
  val MinUsernameLength: Int = 3
  val MinPasswordLength: Int = 6
  val MaxUsernameLength: Int = 20
  val MaxPasswordLength: Int = 20

  private val EmailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r
  private val UsernameRegex = "^[a-zA-Z0-9._-]+".r

  // Username validation function
  def validateUsername(username: String): Either[String, String] =
    if (username.length < MinUsernameLength)
      Left(s"Username must be at least $MinUsernameLength characters long")
    else if (!UsernameRegex.matches(username))
      Left(s"Username cannot contain spaces or forbidden symbols")
    else
      Right(username)

  // Email validation function
  def validateEmail(email: String): Either[String, String] =
    if EmailRegex.matches(email) then Right(email)
    else Left("Invalid email format")

  // Password validation function
  def validatePassword(password: String): Either[String, String] =
    if (password.length < MinPasswordLength)
      Left(s"Password must be at least $MinPasswordLength characters long")
    else
      Right(password)

  // Encoders
  implicit val usernameIdentityEncoder: JsonEncoder[UsernameIdentity] = DeriveJsonEncoder.gen[UsernameIdentity]
  implicit val emailIdentityEncoder: JsonEncoder[EmailIdentity] = DeriveJsonEncoder.gen[EmailIdentity]

  // Decoders with validation
  implicit val usernameIdentityDecoder: JsonDecoder[UsernameIdentity] =
    DeriveJsonDecoder.gen[UsernameIdentity].mapOrFail(_.validate)

  implicit val emailIdentityDecoder: JsonDecoder[EmailIdentity] =
    DeriveJsonDecoder.gen[EmailIdentity].mapOrFail(_.validate)

  // todo add Combined identity encoder

  def fromUsername(username: String, password: String): Either[String, Identity] =
    UsernameIdentity(username, password).validate

  def fromEmail(email: String, password: String): Either[String, Identity] =
    EmailIdentity(email, password).validate

  def validate(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)
