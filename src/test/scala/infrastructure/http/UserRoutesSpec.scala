package irka.grilleEncoder
package infrastructure.http

import domain.model.User
import infrastructure.db.repository.user.UserRepository
import zio.Scope
import zio.test.*
import zio.http.*
import zio.json.*
import zio.test.Assertion.*
import zio.ZIO
import zio.ZLayer

object UserRoutesSpec extends ZIOSpecDefault {
  val usersMock = List(User("1", "user1", List.empty))
  val userRepositoryMock: UserRepository = new UserRepository {
    override def get: ZIO[Any, Throwable, List[User]] = ZIO.succeed(usersMock)

    override def create(user: User): ZIO[Any, Throwable, List[Long]] = ???

    override def update(user: User): ZIO[Any, Throwable, List[Long]] = ???

    override def delete(user: User): ZIO[Any, Throwable, User] = ???
  }

  def spec: Spec[Any, Throwable] =
    test("users test") {
      for {
        client <- ZIO.service[Client]
        _ <- TestClient.addRoutes[UserRepository] {
          UserRoutes.getUsers
        }
        usersResponse <- client.batched(Request.get(URL.root / "users"))
        usersBody <- usersResponse.body.asString
      } yield assertTrue(usersBody == usersMock.toJson)
    }.provide(TestClient.layer, ZLayer.succeed(userRepositoryMock))
}
