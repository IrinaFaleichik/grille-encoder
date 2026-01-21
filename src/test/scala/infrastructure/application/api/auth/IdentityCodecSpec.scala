package irka.grilleEncoder
package infrastructure.application.api.auth

import zio.test.{Spec, ZIOSpecDefault}
import application.api.auth._
import zio.json._
import zio.test._
import zio.test.Assertion._

object IdentityCodecSpec extends ZIOSpecDefault:
  def spec: Spec[Any, Nothing] = suite("JSON serialization")(
    test("UsernameIdentity encoder/decoder roundtrip"):
      val identity = UsernameIdentity("validUser", "password123")
      val json = identity.toJson
      val decoded = json.fromJson[UsernameIdentity]
      assert(decoded)(isRight(equalTo(identity)))
    ,
    test("EmailIdentity encoder/decoder roundtrip"):
      val identity = EmailIdentity("user@example.com", "password123")
      val json = identity.toJson
      val decoded = json.fromJson[EmailIdentity]
      assert(decoded)(isRight(equalTo(identity)))
    ,
    test("UsernameIdentity decoder validates username"):
      val json = """{"username": "ab", "password": "password123"}"""
      val decoded = json.fromJson[UsernameIdentity]
      assert(decoded.left.toOption.get)(containsString("at least"))
    ,
    test("UsernameIdentity decoder validates password"):
      val json = """{"username": "validUser", "password": "pass"}"""
      val decoded = json.fromJson[UsernameIdentity]
      assert(decoded.left.toOption.get)(containsString("Password must be at least"))
    ,
    test("EmailIdentity decoder validates email"):
      val json = """{"email": "not-an-email", "password": "password123"}"""
      val decoded = json.fromJson[EmailIdentity]
      assert(decoded.left.toOption.get)(containsString("Invalid email format"))
    ,
    test("EmailIdentity decoder validates password"):
      val json = """{"email": "user@example.com", "password": "pass"}"""
      val decoded = json.fromJson[EmailIdentity]
      assert(decoded.left.toOption.get)(containsString("Password must be at least"))
  )

