package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, TableEntity}

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{ZIO, ZLayer}

import java.sql.SQLException

// TODO code injection for this basic repositories to reduce code duplication?
final class UserRepositoryDefault(ctx: DBContext) extends UserRepository:

  import ctx.*

  private def insertValues(values: List[TableEntity.User]) = quote:
    liftQuery(values).foreach(v => query[TableEntity.User].insertValue(v))

  private def insert(batch: List[TableEntity.User]) =
    ctx.run(insertValues(batch))

  override def create(user: User): ZIO[Any, SQLException, List[Long]] =
    ZIO.logInfo(s"Creating user: ${user.name}")
    insert(List(toRow(user)))

  override def get: ZIO[Any, SQLException, List[User]] =
    def getRow: ZIO[Any, SQLException, List[TableEntity.User]] = ctx.run(query[TableEntity.User])

    ZIO.logInfo(s"Get all users")
    getRow.map(_.map(toDomain))

  override def update(user: User): ZIO[Any, SQLException, List[Long]] =
    def updateUser(u: List[TableEntity.User]) = quote:
      liftQuery(u).foreach(v => query[TableEntity.User].updateValue(v))

    ZIO.logInfo(s"Updating user: ${user.name}")
    ctx.run(updateUser(List(toRow(user))))

  override def delete(user: User): ZIO[Any, SQLException, List[Long]] =
    // todo add case if user that we want to delete doesn't exist
    def deleteUser(u: List[TableEntity.User]) = quote:
      liftQuery(u).foreach(v => query[TableEntity.User].filter(_.id == v.id).delete)

    ZIO.logInfo(s"Deleting user: ${user.name}")
    ctx.run(deleteUser(List(toRow(user))))


object UserRepositoryDefault:

  lazy val live: ZLayer[DBContext, Nothing, UserRepositoryDefault] =
    ZIO.logInfo(s"Initialize Data Access Layer for UserRepositoryDefault...")
    ZLayer.fromFunction(new UserRepositoryDefault(_))
