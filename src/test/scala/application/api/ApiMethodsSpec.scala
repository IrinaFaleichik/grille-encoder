package irka.grilleEncoder
package application.api

import application.api.*
import application.api.auth.identity.*
import domain.errors.InvalidJson
import domain.model.{User, UserId}

import irka.grilleEncoder.application.api.auth.dto.Role
import zio.*
import zio.http.*
import zio.http.Status.*
import zio.json.*
import zio.test.*
import zio.test.Assertion.*

import java.sql.SQLException

object ApiMethodsSpec extends ZIOSpecDefault:

  // Test data
  val testUser = User("test-id", "test-user")
  val testUserId = new UserId("test-id")
  val validUsernameIdentity = UsernameIdentity("testuser", "password123")
  val validEmailIdentity = EmailIdentity("user@example.com", "password123")

  // test class for failure/default formatting
  class UnknownParsingType

  def spec: Spec[Any, Throwable] = suite("application.api package")(
    suite("Error Handling")(
      test("anyError converts any exception to internal server error response"):
        val givenException = new RuntimeException("Test error")
        val response = anyError(givenException)

        assertTrue(
          response.status == Status.InternalServerError,
          response.body.toString.contains("DB error")
        )
      ,
      test("jsonParsingError converts InvalidJson to bad request response"):
        val givenJsonError = InvalidJson("Invalid JSON syntax")("""{"example":"json"}""")
        val response = jsonParsingError(givenJsonError)

        assertTrue(
          response.status == Status.BadRequest,
          response.body.toString.contains("Invalid JSON")
        )
      ,
      test("dbErrors converts SQLException to internal server error response"):
        val givenSqlException = new SQLException("Database connection failed")
        val response = (dbErrors + anyError)(givenSqlException)

        assertTrue(
          response.status == Status.InternalServerError,
          response.body.toString.contains("Database connection failed")
        )
      ,
      test("error handlers can be combined with + operator"):
        val jsonError = InvalidJson("Invalid JSON syntax")("""{"example":"json"}""")
        val combinedHandler = jsonParsingError + anyError
        val response = combinedHandler(jsonError)

        assertTrue(
          response.status == Status.BadRequest,
          response.body.toString.contains("Invalid JSON")
        )
    ),

    suite("Request Body Parsing")(
      test("parseRequestBody successfully parses valid JSON"):
        val validJson = testUser.toJson
        val request = Request.post(
          URL.empty,
          Body.fromString(validJson)
        )
        val result = parseRequestBody[User](request)
        assertZIO(result)(equalTo(testUser))
      ,
      test("parseRequestBody fails with InvalidJson for invalid JSON"):
        val invalidJson = "{invalid:json}"
        val request = Request.post(
          URL.empty,
          Body.fromString(invalidJson)
        )
        val result = parseRequestBody[User](request).exit
        assertZIO(result)(fails(isSubtype[InvalidJson](anything)))
      ,
      test("formatExampleFor returns appropriate example JSON for different types"):
        // Check examples for different types
        assertTrue(
          formatExampleFor[UserId].contains("example-id"),
          formatExampleFor[Role].contains("User"),
          formatExampleFor[User].contains("example-name"),
          formatExampleFor[UnknownParsingType] == "{ }" // Default case
        )
    )
    ,

    suite("Credential Parsing")(
      test("parseCredentials successfully parses EmailIdentity"):
        val emailJson = validEmailIdentity.toJson
        val request = Request.post(
          URL.empty,
          Body.fromString(emailJson)
        )
        val result = parseCredentials(request)
        assertZIO(result)(isSubtype[EmailIdentity](anything))
      ,
      test("parseCredentials successfully parses UsernameIdentity"):
        val usernameJson = validUsernameIdentity.toJson
        val request = Request.post(
          URL.empty,
          Body.fromString(usernameJson)
        )
        val result = parseCredentials(request)
        assertZIO(result)(isSubtype[Identity](anything))
      ,
      test("parseCredentials falls back to UsernameIdentity if EmailIdentity fails"):
        // Given a JSON that looks like a UsernameIdentity but not an EmailIdentity
        val json = """{"username":"testuser","password":"password123"}"""
        val request = Request.post(
          URL.empty,
          Body.fromString(json)
        )
        val result = parseCredentials(request)
        assertZIO(result)(isSubtype[UsernameIdentity](anything))
      ,
      test("parseCredentials fails if neither EmailIdentity nor UsernameIdentity can be parsed"):
        val invalidJson = """{"foo":"bar"}"""
        val request = Request.post(
          URL.empty,
          Body.fromString(invalidJson)
        )
        val result = parseCredentials(request).exit
        assertZIO(result)(fails(anything))
    )
  )

  // Helper method to make formatExampleFor visible for testing
  private def formatExampleFor[T: scala.reflect.ClassTag]: String =
    application.api.formatExampleFor[T]
