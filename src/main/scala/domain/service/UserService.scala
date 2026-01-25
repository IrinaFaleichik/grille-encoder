package irka.grilleEncoder
package domain.service

import domain.model.{User, UserId}

import infrastructure.db.repository.user.UserRepositoryDefault
import zio.{ZIO, ZLayer}

trait UserService:
  // Admin operations
  def listAllUsers(): ZIO[Any, Throwable, List[User]]

  def createUser(user: User): ZIO[Any, Throwable, User]

  def updateUser(user: User): ZIO[Any, Throwable, User]

  def deleteUser(userId: UserId): ZIO[Any, Throwable, Boolean]

  // Account operations (for authenticated users)
  def getUserProfile(userId: UserId): ZIO[Any, Throwable, Option[User]]

  def updateOwnProfile(userId: UserId, user: User): ZIO[Any, Throwable, User]

  // Account cardboard operations
  //  def createNewCardboard(userId: UserId, cardboardSource: CardboardSource): ZIO[CardboardService, Throwable, User]
  //  def deleteCardboard(userId: UserId, cardboardId: CardboardId): ZIO[Any, Throwable, User]
  //  def updateCardboard(userId: UserId, cardboard: Cardboard): ZIO[Any, Throwable, User]

  // Public operations
  def registerNewUser(user: User): ZIO[Any, Throwable, User]

object UserService:
  final class UserServiceLive(userRepoDefault: UserRepositoryDefault) extends UserService:
    override def listAllUsers(): ZIO[Any, Throwable, List[User]] =
      userRepoDefault.get

    override def createUser(user: User): ZIO[Any, Throwable, User] =
      userRepoDefault.create(user).map(_ => user) //todo return created user

    override def updateUser(user: User): ZIO[Any, Throwable, User] =
      userRepoDefault.update(user).map(_ => user) //todo return updated user

    override def deleteUser(userId: UserId): ZIO[Any, Throwable, Boolean] =
      userRepoDefault.delete(userId).map(_ => true) //todo return boolean

    override def getUserProfile(userId: UserId): ZIO[Any, Throwable, Option[User]] = ???//findById(userId)

    override def updateOwnProfile(userId: UserId, user: User): ZIO[Any, Throwable, User] = updateUser(user)

    override def registerNewUser(user: User): ZIO[Any, Throwable, User] = createUser(user)

  val live: ZLayer[UserRepositoryDefault, Nothing, UserService] =
    ZLayer.fromFunction(new UserServiceLive(_))

