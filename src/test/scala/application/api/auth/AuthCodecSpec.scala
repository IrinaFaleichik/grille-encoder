package irka.grilleEncoder
package application.api.auth

import application.api.auth.{AuthUser, PasswordHash, Role}
import domain.model.UserId

import zio.json.*
import zio.test.*
import zio.test.Assertion.*

object AuthCodecSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Nothing] = suite("Auth Codecs")(
    suite("AuthUser Codec")(
      test("encodes AuthUser to JSON") {
        val authUser = AuthUser("user1": UserId, "admin", PasswordHash.fromPlainText("password123"), Some("admin@example.com"), Role.Admin)
        val json = authUser.toJson
        assertTrue(
          json.contains(""""id":"user1"""") &&
            json.contains(""""username":"admin"""") &&
            json.contains(""""role":"Admin"""") &&
            json.contains(""""email":"admin@example.com"""")
        )
      },
      test("decodes AuthUser from JSON") {
        val json = """{"id":"user1","username":"admin","password":"password123","email":"admin@example.com","role":"Admin"}"""
        val authUser = json.fromJson[AuthUser]
        assertTrue(authUser.isRight)
      },
      test("roundtrip AuthUser encoding/decoding") {
        val authUser = AuthUser("user1": UserId, "admin", PasswordHash.fromPlainText("password123"), Some("admin@example.com"), Role.Admin)
        val encoded = authUser.toJson
        val decoded = encoded.fromJson[AuthUser]
        assertTrue(decoded.isRight && decoded.toOption.get.username == authUser.username)
      }
    ),
    //    suite("AuthUserDto Codec")(
    //      test("encodes AuthUserDto to JSON") {
    //        val authUserDto = AuthUserDto("user1": UserId, "admin", Some("admin@example.com"), Role.Admin)
    //        val json = authUserDto.toJson
    //        assertTrue(
    //          json.contains(""""id":"user1"""") &&
    //            json.contains(""""username":"admin"""") &&
    //            json.contains(""""role":"Admin"""") &&
    //            json.contains(""""email":"admin@example.com"""")
    //        )
    //      },
    //      test("decodes AuthUserDto from JSON") {
    //        val json = """{"id":"user1","username":"admin","email":"admin@example.com","role":"Admin"}"""
    //        val authUserDto = json.fromJson[AuthUserDto]
    //        assertTrue(authUserDto.isRight)
    //      },
    //      test("roundtrip AuthUserDto encoding/decoding") {
    //        val authUserDto = AuthUserDto(UserId("user1"), "admin", Some("admin@example.com"), Role.Admin)
    //        val encoded = authUserDto.toJson
    //        val decoded = encoded.fromJson[AuthUserDto]
    //        assertTrue(decoded == Right(authUserDto))
    //      }
    //    ),
    //    suite("AuthRequest Codec")(
    //      test("encodes AuthRequest to JSON") {
    //        val authRequest = AuthRequest("admin", "password123")
    //        val json = authRequest.toJson
    //        assertTrue(
    //          json.contains(""""username":"admin"""") &&
    //            json.contains(""""password":"password123"""")
    //        )
    //      },
    //      test("decodes AuthRequest from JSON") {
    //        val json = """{"username":"admin","password":"password123"}"""
    //        val authRequest = json.fromJson[AuthRequest]
    //        assertTrue(authRequest.isRight)
    //      },
    //      test("roundtrip AuthRequest encoding/decoding") {
    //        val authRequest = AuthRequest("admin", "password123")
    //        val encoded = authRequest.toJson
    //        val decoded = encoded.fromJson[AuthRequest]
    //        assertTrue(decoded == Right(authRequest))
    //      }
    //    ),
    suite("Role Codec")(
      test("encodes Role.Admin to JSON") {
        val role = Role.Admin
        val json = role.toJson
        assertTrue(json.contains(""""Admin""""))
      },
      test("encodes Role.User to JSON") {
        val role = Role.User
        val json = role.toJson
        assertTrue(json.contains(""""User""""))
      },
      test("decodes Role.Admin from JSON") {
        val json = """"Admin""""
        val role = json.fromJson[Role]
        assertTrue(role == Right(Role.Admin))
      },
      test("decodes Role.User from JSON") {
        val json = """"User""""
        val role = json.fromJson[Role]
        assertTrue(role == Right(Role.User))
      },
      test("roundtrip Role encoding/decoding") {
        val roles = List(Role.User, Role.Admin)
        assertTrue(
          roles.forall { role =>
            val encoded = role.toJson
            val decoded = encoded.fromJson[Role]
            decoded == Right(role)
          }
        )
      }
    )
  )
}