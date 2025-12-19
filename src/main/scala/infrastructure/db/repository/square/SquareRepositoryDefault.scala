package irka.grilleEncoder
package infrastructure.db.repository.square

import domain.model.{Square, Cardboard}
import infrastructure.db.entities.{TableEntity, DBContext}
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{Task, ZIO, ZLayer}
import java.sql.SQLException

// TODO code injection for this basic repositories to reduce code duplication?
final class SquareRepositoryDefault(ctx: DBContext) extends SquareRepository {

  import ctx.*

  private def insertValues(values: List[TableEntity.Square]) = quote {
    liftQuery(values).foreach(v => query[TableEntity.Square].insertValue(v))
  } // todo make function for 1 value insertion or not?

  private def insert(batch: List[TableEntity.Square]) = {
    ctx.run(insertValues(batch))
  }

  override def create(square: Square): ZIO[Any, SQLException, List[Long]] = insert(List(toRow(square)))

  private def getHelper: ZIO[Any, SQLException, List[TableEntity.Square]] = ctx.run(query[TableEntity.Square])
  override def get: ZIO[Any, SQLException, List[Square]] = getHelper.map(_.map(toDomain))

  override def update(square: Square): ZIO[Any, SQLException, Square] = ???

  override def delete(square: Square): ZIO[Any, SQLException, Square] = ??? // todo return Square or number of deleted Squares + CHECK THE DELETE IN OBJECT

}

object SquareRepositoryDefault {

  def create(square: Square): ZIO[SquareRepositoryDefault, SQLException, List[Long]] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.create(square))
  }

  def get: ZIO[SquareRepositoryDefault, SQLException, List[Square]] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.get)
  }

  def update(square: Square): ZIO[SquareRepositoryDefault, SQLException, Square] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.update(square))
  }

  def delete(square: Square): ZIO[SquareRepositoryDefault, SQLException, Square] = {
    ZIO.serviceWithZIO[SquareRepositoryDefault](_.update(square))
  }

  lazy val live: ZLayer[DBContext, Nothing, SquareRepositoryDefault] = ZLayer.fromFunction(new SquareRepositoryDefault(_))
}
