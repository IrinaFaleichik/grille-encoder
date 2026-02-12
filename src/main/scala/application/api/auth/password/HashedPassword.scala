package irka.grilleEncoder
package application.api.auth.password

import zio.json.*
import java.security.MessageDigest
import java.util.Base64
import scala.annotation.tailrec

case class HashedPassword private(private[auth] val hash: String):
  override def toString: String = "Password(***)"

  def verify(hashedPassword: HashedPassword): Boolean =
    this.hash == hashedPassword.hash

  def verifyHashed(hashedPassword: String): Boolean =
    hashedPassword == this.hash

object HashedPassword:
  def create(hash: String): HashedPassword = HashedPassword(hash)