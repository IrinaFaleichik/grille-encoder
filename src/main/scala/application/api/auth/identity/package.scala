package irka.grilleEncoder
package application.api.auth

package object identity:

  // making Identity a package private sealed trait
  private[identity] sealed trait Seal

  private[identity] given Seal with {}

  // parsing constants
  val MinUsernameLength: Int = 3
  val MinPasswordLength: Int = 6
  val MaxUsernameLength: Int = 20
  val MaxPasswordLength: Int = 20

  private val EmailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".r
  private val UsernameRegex = "^[a-zA-Z0-9._-]+".r

  // Validation utilities - accessible throughout the package
  private[identity] def validateUsername(username: String): Either[String, String] =
    if (username.length < MinUsernameLength)
      Left(s"Username must be at least $MinUsernameLength characters long")
    else if (!UsernameRegex.matches(username))
      Left(s"Username cannot contain spaces or forbidden symbols")
    else
      Right(username)

  private[identity] def validateEmail(email: String): Either[String, String] =
    if (EmailRegex.matches(email)) Right(email)
    else Left("Invalid email format")

  private[identity] def validatePassword(password: String): Either[String, String] =
    if (password.length < MinPasswordLength)
      Left(s"Password must be at least $MinPasswordLength characters long")
    else
      Right(password)

  // Factory methods for public use
  def fromUsername(username: String, password: String): Either[String, Identity] =
    UsernameIdentity(username, password).validate

  def fromEmail(email: String, password: String): Either[String, Identity] =
    EmailIdentity(email, password).validate

  // Combined validation that tries both email and username
  def validate(emailOrUsername: String, password: String): Either[String, Identity] =
    EmailIdentity(emailOrUsername, password).validate
      .orElse(UsernameIdentity(emailOrUsername, password).validate)
