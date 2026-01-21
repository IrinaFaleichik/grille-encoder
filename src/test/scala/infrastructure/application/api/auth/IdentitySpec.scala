package irka.grilleEncoder
package infrastructure.application.api.auth

import zio.test.{Spec, ZIOSpecDefault}
import application.api.auth._
import zio.json._
import zio.test._
import zio.test.Assertion._

object IdentitySpec extends ZIOSpecDefault:
  def spec: Spec[Any, Nothing] = suite("IdentitySpec")(
    suite("Factory methods")(
      suite("fromUsername")(
        test("succeeds with valid inputs"):
          val result = Identity.fromUsername("validUser", "password123")
          assertTrue(result.isRight) &&
            assertTrue(result.toOption.get.isInstanceOf[UsernameIdentity]) &&
            assert(result.toOption.get.asInstanceOf[UsernameIdentity].username)(equalTo("validUser"))
        ,
        test("fails with short username"):
          val result = Identity.fromUsername("ab", "password123")
          assertTrue(result.isLeft) &&
            assert(result.left.toOption.get)(containsString("at least"))
        ,
        test("fails with invalid characters"):
          val result = Identity.fromUsername("user name", "password123")
          assertTrue(result.isLeft) &&
            assert(result.left.toOption.get)(containsString("spaces or forbidden symbols"))
        ,
        test("fails with short password"):
          val result = Identity.fromUsername("validUser", "pass")
          assertTrue(result.isLeft) &&
            assert(result.left.toOption.get)(containsString("Password must be at least"))
      ),

      suite("fromEmail")(
        test("succeeds with valid inputs"):
          val result = Identity.fromEmail("user@example.com", "password123")
          assertTrue(result.isRight) &&
            assertTrue(result.toOption.get.isInstanceOf[EmailIdentity]) &&
            assert(result.toOption.get.asInstanceOf[EmailIdentity].email)(equalTo("user@example.com"))
        ,
        test("fails with invalid email format") {
          val result = Identity.fromEmail("not-an-email@", "password123")
          assertTrue(result.isLeft) &&
            assert(result.left.toOption.get)(equalTo("Invalid email format"))
        },

        test("fails with short password"):
          val result = Identity.fromEmail("user@example.com", "pass")
          assertTrue(result.isLeft) &&
            assert(result.left.toOption.get)(containsString("Password must be at least"))
      )
    ),

    // Validation rules tests
    suite("Validation rules")(
      test("username minimum length edge") {
        val minValidLength = "a" * Identity.MinUsernameLength
        val tooShort = "a" * (Identity.MinUsernameLength - 1)

        assertTrue(Identity.fromUsername(minValidLength, "password123").isRight) &&
          assertTrue(Identity.fromUsername(tooShort, "password123").isLeft)
      },

      test("username allowed characters") {
        val validChars = List("user123", "user_name", "uSeR-nAmE", "user.name8")
        val invalidChars = List("user name", "user@name", "user#name", "user+name", "user‿name")

        assertTrue(
          validChars.forall(u => Identity.fromUsername(u, "password123").isRight) &&
            invalidChars.forall(u => Identity.fromUsername(u, "password123").isLeft)
        )
      },

      test("password minimum length") {
        val minValidLength = "a" * Identity.MinPasswordLength
        val tooShort = "a" * (Identity.MinPasswordLength - 1)

        assertTrue(Identity.fromUsername("validUser", minValidLength).isRight) &&
          assertTrue(Identity.fromUsername("validUser", tooShort).isLeft)
      },

      test("email format validation") {
        val validEmails = List("user@example.com", "user.name@example.co.uk", "user+tag@example.com")
        val invalidEmails = List("not-an-email", "user@", "@example.com", "user@.com", "user@example.  ", "user@example...")

        assertTrue(
          validEmails.forall(e => Identity.fromEmail(e, "password123").isRight) &&
            invalidEmails.forall(e => Identity.fromEmail(e, "password123").isLeft)
        )
      }
    )
  )