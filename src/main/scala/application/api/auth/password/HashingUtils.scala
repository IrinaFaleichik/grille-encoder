package irka.grilleEncoder
package application.api.auth.password

import zio.{ZIO, ZLayer}
import zio.json.{JsonDecoder, JsonEncoder}

import java.security.MessageDigest
import java.util.Base64
import scala.annotation.tailrec
import scala.collection.parallel.Task
import scala.meta.Defn.Trait

trait HashingUtils:
  val secret: Secret

  @tailrec
  private def hashing(plainPassword: String, currentIteration: Int): String =
    // Simple SHA-256
    val bytes = MessageDigest.getInstance("SHA-256").digest(plainPassword.getBytes("UTF-8"))
    val hash = Base64.getEncoder.encodeToString(bytes)
    if (currentIteration == secret.iterations) hash
    else hashing(hash, currentIteration + 1)

  def fromPlainText(plainPassword: String): HashedPassword =
    HashedPassword.create(hashing(plainPassword + secret.salt, 0))

object HashingUtils:

  private def make(secret: Secret) =
    new HashingUtils:
      override val secret: Secret = secret

  // JSON codecs for PasswordHash
  implicit val encoder: JsonEncoder[HashedPassword] = JsonEncoder.string.contramap(_.hash)

  implicit val decoder: JsonDecoder[HashedPassword] = JsonDecoder.string.map(HashedPassword.create)

  def live: ZLayer[Secret, Throwable, HashingUtils] =
    ZLayer.fromFunction(HashingUtils.make(_))

  def fromPlainText(plainPassword: String): ZIO[HashingUtils, Throwable, HashedPassword] =
    ZIO.serviceWith[HashingUtils](_.fromPlainText(plainPassword))
