package irka.grilleEncoder
package infrastructure.application.api

import application.api.UserRoutes
import domain.errors.InvalidJson
import domain.model.{User, UserId}
import core.repository.UserRepository

import zio.http.*
import zio.http.Status.BadRequest
import zio.json.*
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, ZIO, ZLayer}

import java.sql.SQLException

object UserRoutesExceptionsSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Throwable] =
    usersRouteTests + userCreateRouteTests + userUpdateRouteTests + userDeleteRouteTests

  /* Mocks for tests */
  val testUser1 = User("example-id", "example-name")
  val expectedJson: String = testUser1.toJson
  val expectedJsonId: String = testUser1.id.toJson

  val invalidJsonErr: InvalidJson = new InvalidJson("(expected \'{\' got \'I\')")(expectedJson)
  val invalidJsonErrDeleteUser: InvalidJson = new InvalidJson("(expected \'\"\' got \'I\')")(expectedJsonId)
  val usersMock = List(testUser1)
  val usersException = SQLException("Some DB error")
  val createdException = SQLException("Some DB error on create")
  val updatedException = SQLException("Some DB error on update")
  val deletedException = SQLException("Some DB error on delete")

  val userRepositorySqlFailureMock: UserRepository = new UserRepository {
    override def get: ZIO[Any, Throwable, List[User]] = ZIO.fail(usersException)

    override def create(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.fail(createdException)

    override def update(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.fail(updatedException)

    override def delete(userid: UserId): ZIO[Any, Throwable, List[Long]] = ZIO.fail(deletedException)
  }

  private val usersRouteTests = suite("http/users") {
    val usersRoute = UserRoutes.getUsers
    val usersRequestRoute = URL.root / "users"
    List(
      test("Db returns error: expect a valid error message and status InternalServerError") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            usersRoute
          }
          usersResponse <- client.batched(Request.get(usersRequestRoute))
          usersStatus = usersResponse.status
          usersBody <- usersResponse.body.asString
        yield assertTrue(
          usersStatus == Status.InternalServerError,
          usersBody == usersException.getMessage
        )
      }.provide(TestClient.layer, ZLayer.succeed(userRepositorySqlFailureMock))
    )
  }

  private val userCreateRouteTests = suite("http/user/create") {
    val createRoute = UserRoutes.createUser
    val createRequestRoute = URL.root / "user" / "create"
    List(
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            createRoute
          }
          createUserResponse <- client.batched(Request.post(
            createRequestRoute,
            Body.fromString("Invalid string")
          ))
          resultStatus = createUserResponse.status
          resultBody <- createUserResponse.body.asString
        yield assertTrue(
          resultBody == invalidJsonErr.getMessage,
          resultStatus == BadRequest
        )
      }
      ,
      test("Db returns error: expect a valid error message and status InternalServerError") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            createRoute
          }
          createUserResponse <- client.batched(Request.post(
            createRequestRoute,
            Body.fromString(testUser1.toJson)
          ))
          resultStatus = createUserResponse.status
          resultBody <- createUserResponse.body.asString
        yield assertTrue(
          resultBody == createdException.getMessage,
          resultStatus == Status.InternalServerError
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySqlFailureMock)))
  }

  private val userUpdateRouteTests = suite("http/user/update") {
    val updateRoute = UserRoutes.updateUser
    val updateRequestRoute = URL.root / "user" / "update"
    List(
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
      },
      test("Db returns error: expect a valid error message and status InternalServerError") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            updateRoute
          }
          updateUserResponse <- client.batched(Request.post(
            updateRequestRoute,
            Body.fromString(testUser1.toJson)
          ))
          resultStatus = updateUserResponse.status
          resultBody <- updateUserResponse.body.asString
        yield assertTrue(
          resultBody == updatedException.getMessage,
          resultStatus == Status.InternalServerError
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySqlFailureMock)))
  }

  private val userDeleteRouteTests = suite("http/user/delete") {
    val deleteRoute = UserRoutes.deleteUser
    val deleteRequestRoute = URL.root / "user" / "delete"
    List(
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository](deleteRoute)
          deleteUserResponse <- client.batched(Request.post(
            deleteRequestRoute,
            Body.fromString("Invalid string")
          ))
          resultStatus = deleteUserResponse.status
          resultBody <- deleteUserResponse.body.asString
        yield assertTrue(
          resultBody == invalidJsonErrDeleteUser.getMessage,
          resultStatus == BadRequest
        )
      }
      ,
      test("Db returns error: expect a valid error message and status InternalServerError") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository](deleteRoute)
          deleteUserResponse <- client.batched(Request.post(
            deleteRequestRoute,
            Body.fromString(testUser1.id.toJson)
          ))
          resultStatus = deleteUserResponse.status
          resultBody <- deleteUserResponse.body.asString
        yield assertTrue(
          resultBody == deletedException.getMessage,
          resultStatus == Status.InternalServerError
        )
      }
    ).map(_.provide(TestClient.layer, ZLayer.succeed(userRepositorySqlFailureMock)))
  }

}
