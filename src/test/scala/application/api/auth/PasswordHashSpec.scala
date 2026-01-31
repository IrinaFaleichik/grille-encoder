package irka.grilleEncoder
package application.api.auth

import application.api.auth.PasswordHash
import zio.test.*
import AuthCodecSpec.suite

object PasswordHashSpec extends ZIOSpecDefault:
  def spec: Spec[Any, Nothing] = suite("Password hashing")(
    test("encoded password is different from original"):
      val originalPassword = "12345678"
      val passwordHash = PasswordHash.fromPlainText(originalPassword)
      assertTrue(passwordHash.verify(originalPassword)),
    test("encode function is idempotent"):
      val originalPassword = "12345678"
      val passwordHash = PasswordHash.fromPlainText(originalPassword)
      val passwordHash1 = PasswordHash.fromPlainText(originalPassword)
      val passwordHash2 = PasswordHash.fromPlainText(originalPassword)
      val passwordHash3 = PasswordHash.fromPlainText(originalPassword)
      val passwordHash4 = PasswordHash.fromPlainText(originalPassword)
      assertTrue(
        passwordHash.verify(originalPassword) &&
          passwordHash1.verify(originalPassword) &&
          passwordHash2.verify(originalPassword) &&
          passwordHash3.verify(originalPassword) &&
          passwordHash4.verify(originalPassword)
      )
  )
