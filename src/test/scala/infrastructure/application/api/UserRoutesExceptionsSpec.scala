package irka.grilleEncoder
package infrastructure.application.api

import application.api.UserRoutes
import domain.errors.InvalidJson
import domain.model.User
import infrastructure.db.repository.user.UserRepository

import zio.http.*
import zio.http.Status.BadRequest
import zio.json.*
import zio.test.*
import zio.test.Assertion.*
import zio.{Scope, ZIO, ZLayer}

import java.sql.SQLException

// todo add parsing json tests
object UserRoutesExceptionsSpec extends ZIOSpecDefault {
  def spec: Spec[Any, Throwable] =
    usersRouteTests + userCreateRouteTests + userUpdateRouteTests

  /* Mocks for tests */
  val invalidJsonErr: InvalidJson = new InvalidJson("(expected \'{\' got \'I\')")
  val testUser1 = User("1", "user1", List.empty)
  val usersMock = List(testUser1)
  val usersException = SQLException("Some DB error")
  val createdException = SQLException("Some DB error on create")
  val updatedException = SQLException("Some DB error on update")

  val userRepositorySqlFailureMock: UserRepository = new UserRepository {
    override def get: ZIO[Any, Throwable, List[User]] = ZIO.fail(usersException)

    override def create(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.fail(createdException)

    override def update(user: User): ZIO[Any, Throwable, List[Long]] = ZIO.fail(updatedException)

    override def delete(user: User): ZIO[Any, Throwable, User] = ???
  }

  private val usersRouteTests = suite("http/users")(
    List(
      test("Db returns error: expect a valid error message and status InternalServerError") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            UserRoutes.getUsers
          }
          usersResponse <- client.batched(Request.get(URL.root / "users"))
          usersStatus = usersResponse.status
          usersBody <- usersResponse.body.asString
        yield assertTrue(
          usersStatus == Status.InternalServerError,
          usersBody == usersException.getMessage
        )
      }.provide(TestClient.layer, ZLayer.succeed(userRepositorySqlFailureMock))
    )
  )

  private val userCreateRouteTests = suite("http/user/create")(
    List(
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            UserRoutes.updateUser
          }
          updateUserResponse <- client.batched(Request.post(
            URL.root / "user" / "update",
            Body.fromString("Invalid string")
          ))
          resultStatus = updateUserResponse.status
          resultBody <- updateUserResponse.body.asString
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
            UserRoutes.createUser
          }
          createUserResponse <- client.batched(Request.post(
            URL.root / "user" / "create",
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
  )

  private val userUpdateRouteTests = suite("http/user/update")(
    List(
      test("JSON invalid input: returns a valid error message and status BadRequest") {
        for
          client <- ZIO.service[Client]
          _ <- TestClient.addRoutes[UserRepository] {
            UserRoutes.updateUser
          }
          updateUserResponse <- client.batched(Request.post(
            URL.root / "user" / "update",
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
            UserRoutes.updateUser
          }
          updateUserResponse <- client.batched(Request.post(
            URL.root / "user" / "update",
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
  )
}
