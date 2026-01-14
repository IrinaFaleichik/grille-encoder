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
final class UserRepositoryDefault(ctx: DBContext) extends UserRepository {

  import ctx.*

  private def insertValues(values: List[TableEntity.User]) = quote {
    liftQuery(values).foreach(v => query[TableEntity.User].insertValue(v))
  } // todo make function for 1 value insertion or not?

  private def insert(batch: List[TableEntity.User]) = {
    ctx.run(insertValues(batch))
  }

  override def create(user: User): ZIO[Any, SQLException, List[Long]] = insert(List(toRow(user)))

  override def get: ZIO[Any, SQLException, List[User]] = {
    def getRow: ZIO[Any, SQLException, List[TableEntity.User]] = ctx.run(query[TableEntity.User])

    getRow.map(_.map(toDomain))
  }

  override def update(user: User): ZIO[Any, SQLException, List[Long]] = {
    def updateUser(u: List[TableEntity.User]) = quote {
      liftQuery(u).foreach(v => query[TableEntity.User].updateValue(v))
    }

    ctx.run(updateUser(List(toRow(user))))
  }

  override def delete(user: User): ZIO[Any, SQLException, User] = ??? // todo return User or number of deleted Users + CHECK THE DELETE IN OBJECT

}

object UserRepositoryDefault {

  //  def create(user: User): ZIO[UserRepositoryDefault, SQLException, List[Long]] = {
  //    ZIO.serviceWithZIO[UserRepositoryDefault](_.create(user))
  //  }
  //
  //  def get: ZIO[UserRepositoryDefault, SQLException, List[User]] = {
  //    ZIO.serviceWithZIO[UserRepositoryDefault](_.get)
  //  }
  //
  //  def update(user: User): ZIO[UserRepositoryDefault, SQLException, List[Long]] = {
  //    ZIO.serviceWithZIO[UserRepositoryDefault](_.update(user))
  //  }
  //
  //  def delete(user: User): ZIO[UserRepositoryDefault, SQLException, User] = {
  //    ZIO.serviceWithZIO[UserRepositoryDefault](_.delete(user))
  //  }

  lazy val live: ZLayer[DBContext, Nothing, UserRepositoryDefault] = ZLayer.fromFunction(new UserRepositoryDefault(_))
}
