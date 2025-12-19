package irka.grilleEncoder
package infrastructure.db.repository.cardboard

import domain.model.Cardboard
import infrastructure.db.entities.{DBContext, TableEntity}

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{Task, ZIO, ZLayer}

import java.sql.SQLException
import scala.language.implicitConversions

final class CardboardRepositoryDefault(ctx: DBContext) extends CardboardRepository(ctx) {

  import ctx.*

  private def insertValues(values: List[TableEntity.Cardboard]) = quote {
    liftQuery(values).foreach(v => query[TableEntity.Cardboard].insertValue(v))
  } // todo make function for 1 value insertion or not?

  private def insert(batch: List[TableEntity.Cardboard]) = {
    ctx.run(insertValues(batch))
  }

  override def create(cardboard: Cardboard): ZIO[Any, SQLException, List[Long]] = insert(List(cardboard))

  override def get: ZIO[Any, SQLException, List[TableEntity.Cardboard]] = ctx.run(query[TableEntity.Cardboard])

  override def update(cardboard: Cardboard): ZIO[Any, SQLException, Cardboard] = ???

  override def delete(cardboard: Cardboard): ZIO[Any, SQLException, Cardboard] = ??? // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}

object CardboardRepositoryDefault {

  // todo check collisions
  def create(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, List[Long]] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.create(cardboard))
  }

  def get: ZIO[CardboardRepositoryDefault, SQLException, List[TableEntity.Cardboard]] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.get)
  }

  def update(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, Cardboard] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.update(cardboard))
  }

  def delete(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, Cardboard] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.update(cardboard))
  }

  lazy val live: ZLayer[DBContext, Nothing, CardboardRepositoryDefault] = ZLayer.fromFunction(new CardboardRepositoryDefault(_))
}
