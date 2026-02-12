package irka.grilleEncoder
package application.api.auth.password

import application.api.auth.password.{HashedPassword, Secret}

import zio.test.*

object PasswordHashSpec extends ZIOSpecDefault:
  val mySecret: Secret =
    new Secret:
      override val salt: String = "my cool salt"
      override val iterations: Int = 42
  val hashingUtils: HashingUtils =
    new HashingUtils:
      override val secret: Secret = mySecret
  def spec: Spec[Any, Nothing] = suite("Password hashing")(
    test("encoded password is different from original"):
      val originalPassword = "12345678"
      val passwordHash: HashedPassword = hashingUtils.fromPlainText(originalPassword)
      assertTrue(passwordHash.verify(hashingUtils.fromPlainText(originalPassword))),
    test("encode function is idempotent"):
      val originalPassword = "12345678"
      val passwordHash = hashingUtils.fromPlainText(originalPassword)
      val passwordHash1 = hashingUtils.fromPlainText(originalPassword)
      val passwordHash2 = hashingUtils.fromPlainText(originalPassword)
      val passwordHash3 = hashingUtils.fromPlainText(originalPassword)
      val passwordHash4 = hashingUtils.fromPlainText(originalPassword)
      assertTrue(
        passwordHash.verify(hashingUtils.fromPlainText(originalPassword)) &&
          passwordHash1.verify(hashingUtils.fromPlainText(originalPassword)) &&
          passwordHash2.verify(hashingUtils.fromPlainText(originalPassword)) &&
          passwordHash3.verify(hashingUtils.fromPlainText(originalPassword)) &&
          passwordHash4.verify(hashingUtils.fromPlainText(originalPassword))
      )
  )
