package irka.grilleEncoder
package infrastructure.application.api

import application.api.UserRoutes
import domain.errors.InvalidJson
import domain.model.User
import core.repository.UserRepository

import zio.{Scope, ZIO, ZLayer}
import zio.http.*
import zio.http.Status.BadRequest
import zio.json.*
import zio.test.*
import zio.test.Assertion.*

object UserRoutesSuccessSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Throwable] =
    usersRouteTests + userCreateRouteTests + userUpdateRouteTests

  /* Mocks for tests */
  val invalidJsonErr: InvalidJson = new InvalidJson("(expected \'{\' got \'I\')")
  val testUser1 = User("1", "user1")
  val usersMock = List(testUser1)
  val created = List(100L)
  val updated = List(10L)
  val deleted = List(1L)
  val createdSuccess: String = "Created user: " + testUser1.toJson
  val updatedSuccess: String = "Updated user: " + testUser1.toJson
  val deletedSuccess: String = "Deleted user: " + testUser1.toJson

  val userRepositorySuccessMock: UserRepository = new UserRepository {
    override def get: ZIO[Any, Throwable, List[User]] = ZIO.succeed(usersMock)

    override def create(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.succeed(created)

    override def update(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.succeed(updated)

    override def delete(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.succeed(deleted)
  }

  private val usersRouteTests = suite("http/users")(
    List(
      test("returns users list as json") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            UserRoutes.getUsers
          }
          usersResponse <- client.batched(Request.get(URL.root / "users"))
          usersBody <- usersResponse.body.asString
        yield assertTrue(usersBody == usersMock.toJson)
      }.provide(TestClient.layer, ZLayer.succeed(userRepositorySuccessMock))
    )
  )

  private val userCreateRouteTests = suite("http/user/create") {
    val createRoute: Route[UserRepository, Response] = UserRoutes.createUser
    val createRequestRoute = URL.root / "user" / "create"
    List(
      test("creates single user and returns it's data") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            createRoute
          }
          createUserResponse <- client.batched(Request.post(
            createRequestRoute,
            Body.fromString(testUser1.toJson)
          ))
          createUserResponse <- createUserResponse.body.asString
        yield assertTrue(createUserResponse == createdSuccess)
      }
      ,
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            createRoute
          }
          updateUserResponse <- client.batched(Request.post(
            createRequestRoute,
            Body.fromString("Invalid string")
          ))
          resultStatus = updateUserResponse.status
          resultBody <- updateUserResponse.body.asString
        yield assertTrue(
          resultBody == invalidJsonErr.getMessage,
          resultStatus == BadRequest
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySuccessMock)))
  }

  private val userUpdateRouteTests = suite("http/user/update") {
    val updateRoute: Route[UserRepository, Response] = UserRoutes.updateUser
    val updateRequestRoute = URL.root / "user" / "update"
    List(
      test("updates single user and returns it's data") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            updateRoute
          }
          updateUserResponse <- client.batched(Request.post(
            updateRequestRoute,
            Body.fromString(testUser1.toJson)
          ))
          updateUserResponse <- updateUserResponse.body.asString
        yield assertTrue(updateUserResponse == updatedSuccess)
      }
      ,
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            updateRoute
          }
          updateUserResponse <- client.batched(Request.post(
            updateRequestRoute,
            Body.fromString("Invalid string")
          ))
          resultStatus = updateUserResponse.status
          resultBody <- updateUserResponse.body.asString
        yield assertTrue(
          resultBody == invalidJsonErr.getMessage,
          resultStatus == BadRequest
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySuccessMock)))
  }

  private val userDeleteRouteTests = suite("http/user/delete") {
    val deleteRoute: Route[UserRepository, Response] = UserRoutes.deleteUser
    val deleteRequestRoute = URL.root / "user" / "delete"
    List(
      test("deletes single user and returns it's data") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            deleteRoute
          }
          deleteUserResponse <- client.batched(Request.post(
            deleteRequestRoute,
            Body.fromString(testUser1.toJson)
          ))
          deleteUserResponse <- deleteUserResponse.body.asString
        yield assertTrue(deleteUserResponse == deletedSuccess)
      }
      ,
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            deleteRoute
          }
          deleteUserResponse <- client.batched(Request.post(
            deleteRequestRoute,
            Body.fromString("Invalid string")
          ))
          resultStatus = deleteUserResponse.status
          resultBody <- deleteUserResponse.body.asString
        yield assertTrue(
          resultBody == invalidJsonErr.getMessage,
          resultStatus == BadRequest
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySuccessMock)))
  }

}
