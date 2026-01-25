package irka.grilleEncoder
package core.repository

import domain.model.{User, UserId}
import infrastructure.db.entities.TableEntity

import zio.ZIO

import scala.language.implicitConversions

trait UserRepository:

  /** conversions between DB and Domain entities */
  def toRow(user: User): TableEntity.User = TableEntity.User(user.id, user.name)

  def toDomain(user: TableEntity.User): User = User(user.id, user.name)

  /** DB operations */
  def create(user: User): ZIO[Any, Throwable, List[Long]]

  def get: ZIO[Any, Throwable, List[User]]

  def update(user: User): ZIO[Any, Throwable, List[Long]]

  def delete(user: UserId): ZIO[Any, Throwable, List[Long]]


object UserRepository:
  def create(user: User): ZIO[UserRepository, Throwable, List[Long]] =
    ZIO.serviceWithZIO[UserRepository](_.create(user))

  def get: ZIO[UserRepository, Throwable, List[User]] =
    ZIO.serviceWithZIO[UserRepository](_.get)

  def update(user: User): ZIO[UserRepository, Throwable, List[Long]] =
    ZIO.serviceWithZIO[UserRepository](_.update(user))

  def delete(user: UserId): ZIO[UserRepository, Throwable, List[Long]] =
    ZIO.serviceWithZIO[UserRepository](_.delete(user))

