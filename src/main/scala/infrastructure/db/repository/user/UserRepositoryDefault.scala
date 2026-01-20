package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, TableEntity}

import io.getquill.*
import io.getquill.jdbczio.Quill
import core.repository.UserRepository
import zio.IsSubtypeOfError.impl
import zio.{ZIO, ZLayer}

import java.sql.SQLException

final class UserRepositoryDefault(ctx: DBContext) extends UserRepository:

  import ctx.*

  private def insert(batch: List[TableEntity.User]): ZIO[Any, java.sql.SQLException, List[Long]] = for
    _ <- ZIO.logInfo(s"Creating users: $batch")
    result <- ctx.run:
      quote:
        liftQuery(batch).foreach(v => query[TableEntity.User].insertValue(v))
  yield result

  override def create(user: User): ZIO[Any, SQLException, List[Long]] =
    insert(List(toRow(user)))

  override def get: ZIO[Any, SQLException, List[User]] = for
    _ <- ZIO.logInfo(s"Get all users")
    result <- ctx.run(query[TableEntity.User])
    _ <- ZIO.logInfo(s"Got users: $result")
  yield result
    .map(toDomain)


  override def update(user: User): ZIO[Any, SQLException, List[Long]] =
    def updateUser(u: List[TableEntity.User]) = quote:
      liftQuery(u).foreach(v => query[TableEntity.User].updateValue(v))

    for
      _ <- ZIO.logInfo(s"Updating user: ${user.name}")
      result <- ctx.run(updateUser(List(toRow(user))))
    yield result

  override def delete(user: User): ZIO[Any, SQLException, List[Long]] = {
    def deleteUser(u: List[TableEntity.User]) = quote:
      liftQuery(u).foreach(v => query[TableEntity.User].filter(_.id == v.id).delete)

    for
      _ <- ZIO.logInfo(s"Deleting user: ${user.name}")
      result: List[Long] <- ctx.run(deleteUser(List(toRow(user))))
    yield result
  }.flatMap: result =>
    if result.isEmpty || result.head == 0
    then ZIO.fail(new SQLException(s"Couldn't delete user: $user is not found"))
    else ZIO.succeed(result)


object UserRepositoryDefault:

  lazy val live: ZLayer[DBContext, Nothing, UserRepositoryDefault] =
    ZLayer.fromZIO(
      for
        _ <- ZIO.logInfo(s"Initialize Data Access Layer for UserRepositoryDefault...")
        ctx <- ZIO.service[DBContext]
      yield new UserRepositoryDefault(ctx)
    )
