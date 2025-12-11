package irka.grilleEncoder
package infrastructure.db.repository.square

import domain.model.{Square, Cardboard}
import infrastructure.db.entities.{SquareRow, DBContext}
import infrastructure.db.{DataService, Students}
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{Task, ZIO, ZLayer}
import java.sql.SQLException

// TODO code injection for this basic repositories to reduce code duplication?
final class SquareRepositoryDefault(ctx: DBContext) extends SquareRepository {

  import ctx.*

  private def insertValues(values: List[SquareRow]) = quote {
    liftQuery(values).foreach(v => query[SquareRow].insertValue(v))
  } // todo make function for 1 value insertion or not?

  private def insert(batch: List[SquareRow]) = {
    ctx.run(insertValues(batch))
  }

  override def create(square: Square): ZIO[Any, SQLException, List[Long]] = insert(List(toRow(square)))

  override def get: ZIO[Any, SQLException, List[SquareRow]] = ctx.run(query[SquareRow])

  override def update(square: Square): ZIO[Any, SQLException, SquareRow] = ???

  override def delete(square: Square): ZIO[Any, SQLException, SquareRow] = ??? // todo return Square or number of deleted Squares + CHECK THE DELETE IN OBJECT

}

object SquareRepositoryDefault {

  def create(square: Square): ZIO[SquareRepositoryDefault, SQLException, List[Long]] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.create(square))
  }

  def get: ZIO[SquareRepositoryDefault, SQLException, List[SquareRow]] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.get)
  }

  def update(square: Square): ZIO[SquareRepositoryDefault, SQLException, SquareRow] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.update(square))
  }

  def delete(square: Square): ZIO[SquareRepositoryDefault, SQLException, SquareRow] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.update(square))
  }

  lazy val live: ZLayer[DBContext, Nothing, SquareRepositoryDefault] = ZLayer.fromFunction(new SquareRepositoryDefault(_))
}
