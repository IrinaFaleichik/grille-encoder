package irka.grilleEncoder
package application.api.auth

import application.api.auth.PasswordHash.fromPlainText
import zio.json.*

import java.security.MessageDigest
import java.util.Base64
import scala.annotation.tailrec

case class PasswordHash private(private val hash: String):
  override def toString: String = "Password(***)"

  def verify(plainPassword: String): Boolean =
    // For the simple SHA-256 implementation:
    val inputHash = fromPlainText(plainPassword)
    inputHash.hash == hash

object PasswordHash:
  private val salt = "my cool salt"
  private val iterations = 42

  @tailrec
  private def hashing(plainPassword: String, currentIteration: Int): String =
    // Simple SHA-256
    val bytes = MessageDigest.getInstance("SHA-256").digest(plainPassword.getBytes("UTF-8"))
    val hash = Base64.getEncoder.encodeToString(bytes)
    if (currentIteration == iterations) return hash
    hashing(hash, currentIteration + 1)

  def fromPlainText(plainPassword: String): PasswordHash =
    PasswordHash(hashing(plainPassword + salt, 0))

  // JSON codecs for PasswordHash
  implicit val encoder: JsonEncoder[PasswordHash] = JsonEncoder.string.contramap(_.hash)

  implicit val decoder: JsonDecoder[PasswordHash] = JsonDecoder.string.map(PasswordHash(_))


