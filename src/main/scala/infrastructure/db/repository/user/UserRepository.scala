package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, TableEntity}

import zio.ZIO

import java.sql.SQLException
import scala.language.implicitConversions

// shadows core.repository.UserRepository
trait UserRepository extends core.repository.UserRepository {

  def toRow(user: User): TableEntity.User = TableEntity.User(user.id, user.name)

  def toDomain(user: TableEntity.User): User = User(user.id, user.name, Nil)

  def create(user: User): ZIO[Any, Throwable, List[Long]]

  def get: ZIO[Any, Throwable, List[User]]

  def update(user: User): ZIO[Any, Throwable, List[Long]]

  def delete(user: User): ZIO[Any, Throwable, User]
}

object UserRepository {
  def create(user: User): ZIO[UserRepository, Throwable, List[Long]] = {
    ZIO.serviceWithZIO[UserRepository](_.create(user))
  }

  def get: ZIO[UserRepository, Throwable, List[User]] = {
    ZIO.serviceWithZIO[UserRepository](_.get)
  }

  def update(user: User): ZIO[UserRepository, Throwable, List[Long]] = {
    ZIO.serviceWithZIO[UserRepository](_.update(user))
  }

  def delete(user: User): ZIO[UserRepository, Throwable, User] = {
    ZIO.serviceWithZIO[UserRepository](_.delete(user))
  }

}