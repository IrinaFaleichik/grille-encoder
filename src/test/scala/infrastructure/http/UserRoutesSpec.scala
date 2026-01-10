package irka.grilleEncoder
package infrastructure.http

import domain.model.User
import infrastructure.db.repository.user.UserRepository

import io.netty.handler.codec.http.multipart.HttpData
import zio.Scope
import zio.test.*
import zio.http.*
import zio.json.*
import zio.test.Assertion.*
import zio.ZIO
import zio.ZLayer

// todo add parsing json tests
object UserRoutesSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Throwable] = usersRouteTests + userCreateRouteTests

  val testUser1 = User("1", "user1", List.empty)
  val usersMock = List(testUser1)
  val created = List(100)
  val userRepositoryMock: UserRepository = new UserRepository {
    override def get: ZIO[Any, Throwable, List[User]] = ZIO.succeed(usersMock)

    override def create(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.succeed(List(100))

    override def update(user: User): ZIO[Any, Throwable, List[Long]] = ???

    override def delete(user: User): ZIO[Any, Throwable, User] = ???
  }


  private val usersRouteTests = suite("http/users")(List(
    test("returns users list as json") {
      for {
        client <- ZIO.service[Client]
        _ <- TestClient.addRoutes[UserRepository] {
          UserRoutes.getUsers
        }
        usersResponse <- client.batched(Request.get(URL.root / "users"))
        usersBody <- usersResponse.body.asString
      } yield assertTrue(usersBody == usersMock.toJson)
    }.provide(TestClient.layer, ZLayer.succeed(userRepositoryMock)),

    test("users route will ignore any json body") {
      for {
        client <- ZIO.service[Client]
        _ <- TestClient.addRoutes[UserRepository] {
          UserRoutes.getUsers
        }
        usersResponse <- client.batched(Request.get(URL.root / "users").withBody(Body.fromString("Some body")))
        usersBody <- usersResponse.body.asString
      } yield assertTrue(usersBody == usersMock.toJson)
    }.provide(TestClient.layer, ZLayer.succeed(userRepositoryMock))
  ))


  private val userCreateRouteTests = suite("http/user/create")(List(
    test("returns users list as json") {
      for {
        client <- ZIO.service[Client]
        _ <- TestClient.addRoutes[UserRepository] {
          UserRoutes.createUser
        }
        createUserResponse <- client.batched(Request.post(
          URL.root / "user" / "create",
          Body.fromString(
            testUser1.toJson
          )
        ))
        createUserResponse <- createUserResponse.body.asString
      } yield assertTrue(createUserResponse == "Created user: " + testUser1.toJson)
    }.provide(TestClient.layer, ZLayer.succeed(userRepositoryMock))
  ))
}
