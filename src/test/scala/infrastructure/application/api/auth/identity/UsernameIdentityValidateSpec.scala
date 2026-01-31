package irka.grilleEncoder
package infrastructure.application.api.auth.identity

import application.api.auth.identity.*

import zio.test.*
import zio.test.Assertion.*
import zio.json.*

object UsernameIdentityValidateSpec extends ZIOSpecDefault:

  def spec: Spec[Any, Nothing] = suite("UsernameIdentity validation")(
    test("should accept valid username and password"):
      val identity = UsernameIdentity("validUser", "validPassword")
      val result = identity.validate
      assertTrue(result.isRight) &&
        assert(result)(isRight(equalTo(identity)))
    ,
    test("should reject username that is too short"):
      val identity = UsernameIdentity("ab", "validPassword") // Less than MinUsernameLength (3)
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("at least"))
    ,
    test("should reject username with invalid characters: space"):
      val identity = UsernameIdentity("invalid user", "validPassword") // Contains space
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("cannot contain spaces"))
    ,
    test("should reject username with invalid characters: %"):
      val identity = UsernameIdentity("invalid%user", "validPassword") // Contains %
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("cannot contain spaces"))
    ,
    test("should reject username with invalid characters: \r"):
      val identity = UsernameIdentity("invalid user", "validPassword") // Contains \r
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("cannot contain spaces"))
    ,
    test("should reject username with invalid characters: unicode character (ascii only)"):
      val identity = UsernameIdentity("user‿name", "validPassword") // Contains unicode
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("cannot contain spaces"))
    ,
    test("should reject empty password"):
      val identity = UsernameIdentity("validUser", "")
      val result = identity.validate
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("Password must be at least 6 characters long"))
    ,
    test("should successfully decode valid JSON"):
      val json = """{"username":"validUser","password":"validPassword"}"""
      val result = json.fromJson[UsernameIdentity]
      assertTrue(result.isRight) &&
        assert(result)(isRight(equalTo(UsernameIdentity("validUser", "validPassword"))))
    ,
    test("should fail to decode invalid JSON with error message"):
      val json = """{"username":"ab","password":"validPassword"}"""
      val result = json.fromJson[UsernameIdentity]
      assertTrue(result.isLeft) &&
        assert(result.left.getOrElse(""))(containsString("Username"))
    ,
    test("should encode to JSON and decode back"):
      val original = UsernameIdentity("validUser", "validPassword")
      val json = original.toJson
      val decoded = json.fromJson[UsernameIdentity]
      assertTrue(decoded.isRight) &&
        assert(decoded)(isRight(equalTo(original)))
    ,
    test("should validate username with special characters correctly"):
      val validIdentity = UsernameIdentity("valid_user-123", "validPassword")
      val invalidIdentity = UsernameIdentity("invalid@user", "validPassword")
      assertTrue(validIdentity.validate.isRight) &&
        assertTrue(invalidIdentity.validate.isLeft)
  )
