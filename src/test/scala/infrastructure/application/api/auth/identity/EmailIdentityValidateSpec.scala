package irka.grilleEncoder
package infrastructure.application.api.auth.identity

import application.api.auth.identity.*

import zio.test.*
import zio.test.Assertion.*
import zio.json.*

object EmailIdentityValidateSpec extends ZIOSpecDefault:

  def spec: Spec[Any, Nothing] = suite("EmailIdentity validation")(
    suite("Email format validation")(
      test("should accept valid email address"):
        val identity = EmailIdentity("user@example.com", "password123")
        val result = identity.validate
        assertTrue(result.isRight) &&
          assert(result)(isRight(equalTo(identity)))
      ,
      test("should reject invalid email format"):
        val invalidEmails = List(
          "userexample.com",    // missing @
          "user@",              // missing domain
          "@example.com",       // missing local part
          "user@example",       // missing TLD
          "user@.com",          // missing domain name
          "user@example."       // incomplete TLD
        )
        val results = invalidEmails.map(email =>
          EmailIdentity(email, "password123").validate
        )
        assertTrue(results.forall(_.isLeft)) &&
          assert(results.head.left.getOrElse(""))(equalTo("Invalid email format"))
    )
    ,
    suite("Password validation")(
      test("should accept valid password"):
        val identity = EmailIdentity("user@example.com", "password123")
        val result = identity.validate
        assertTrue(result.isRight) &&
          assert(result)(isRight(equalTo(identity)))
      ,
      test("should reject password that is too short"):
        val identity = EmailIdentity("user@example.com", "12345") // Less than MinPasswordLength (6)
        val result = identity.validate
        assertTrue(result.isLeft) &&
          assert(result.left.getOrElse(""))(containsString("at least"))
      ,
      test("should reject empty password"):
        val identity = EmailIdentity("user@example.com", "")
        val result = identity.validate

        assertTrue(result.isLeft) &&
          assert(result.left.getOrElse(""))(containsString("Password"))
    ),
    suite("JSON serialization")(
      test("should successfully decode valid JSON"):
        val json = """{"email":"user@example.com","password":"password123"}"""
        val result = json.fromJson[EmailIdentity]

        assertTrue(result.isRight) &&
          assert(result)(isRight(equalTo(EmailIdentity("user@example.com", "password123"))))
      ,
      test("should fail to decode invalid email with error message"):
        val json = """{"email":"invalid-email","password":"password123"}"""
        val result = json.fromJson[EmailIdentity]
        assertTrue(result.isLeft) &&
          assert(result.left.getOrElse(""))(containsString("email"))
      ,
      test("should encode to JSON and decode back"):
        val original = EmailIdentity("user@example.com", "password123")
        val json = original.toJson
        val decoded = json.fromJson[EmailIdentity]
        assertTrue(decoded.isRight) &&
          assert(decoded)(isRight(equalTo(original)))
    ),
    suite("Edge cases")(
      test("should accept email with plus sign in local part"):
        val identity = EmailIdentity("user+tag@example.com", "password123")
        val result = identity.validate
        assertTrue(result.isRight)
      ,
      test("should accept email with subdomain"):
        val identity = EmailIdentity("user@sub.example.com", "password123")
        val result = identity.validate
        assertTrue(result.isRight)
      ,
      test("should accept email with numbers"):
        val identity = EmailIdentity("user123@example123.com", "password123")
        val result = identity.validate
        assertTrue(result.isRight)
    )
  )