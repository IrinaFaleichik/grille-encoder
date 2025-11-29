package irka.grilleEncoder.core.repository

import irka.grilleEncoder.domain.model.User
import zio.*

trait UserRepository {
  def create(user: User): Task[User]
  
  def findById(user: User): Task[Option[User]]
  
  def findAll: Task[List[User]]
  
  def update(user: User): Task[User]
  
  def delete(user: User): Task[Unit] // todo return User or number of deleted users + CHECK THE DELETE IN OBJECT
}

// TODO implement it later
object UserRepository {
  def create(name: String): ZIO[UserRepository, Throwable, User] = ???
  
  def findById(userId: String): ZIO[UserRepository, Throwable, Option[User]] = ???
  
  def findAll: ZIO[UserRepository, Throwable, List[User]] = ???
  
  def update(userId: String, name: String): ZIO[UserRepository, Throwable, User] = ???
  
  def delete(userId: String): ZIO[UserRepository, Throwable, Unit] = ???
}
