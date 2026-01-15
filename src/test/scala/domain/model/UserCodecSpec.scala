package irka.grilleEncoder
package domain.model

import zio.json._
import zio.test._
import zio.test.Assertion._

object UserCodecSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Nothing] = suite("Codecs")(
    suite("User Codec")(
      test("encodes User to JSON") {
        val user = User("user1", "Elizabeth the II", Some(List(Cardboard("card1", "Board", List(), "user1"))))
        val json = user.toJson
        assertTrue(
          json.contains("\"id\":\"user1\"")
            && json.contains("\"name\":\"Elizabeth the II\"")
            && json.contains("""cardboards":[{"id":"card1","name":"Board","squares":[],"userId":"user1"}]""")
        )
      },
      test("encodes User without cardboards to JSON") {
        val user = User("user1", "Elizabeth the II")
        val json = user.toJson
        assertTrue(json.contains("\"id\":\"user1\""))
      },
      test("decodes User from JSON") {
        val json = """{"id":"user1","name":"John Doe","cardboards":[]}"""
        val user = json.fromJson[User]
        assertTrue(user.isRight)
      },
      test("decodes User without cardboards in JSON") {
        val json = """{"id":"user1","name":"John Doe"}"""
        val user = json.fromJson[User]
        assertTrue(user.isRight && user.toOption.get.cardboards.isEmpty)
      },
      test("roundtrip User encoding/decoding") {
        val user = User("user1", "Jane Smith")
        val encoded = user.toJson
        val decoded = encoded.fromJson[User]
        assertTrue(decoded == Right(user))
      }
    )
  )
}
