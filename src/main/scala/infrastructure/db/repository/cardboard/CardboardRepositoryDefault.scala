package irka.grilleEncoder
package infrastructure.db.repository.cardboard

import domain.model.{Cardboard, Square, User}
import infrastructure.db.entities.{CardboardRow, DBContext}
import infrastructure.db.{DataService, Students}

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{Task, ZIO, ZLayer}

import java.sql.SQLException
import scala.language.implicitConversions

final class CardboardRepositoryDefault(ctx: DBContext) extends CardboardRepository(ctx) {

  import ctx.*

  private def insertValues(values: List[CardboardRow]) = quote {
    liftQuery(values).foreach(v => query[CardboardRow].insertValue(v))
  } // todo make function for 1 value insertion or not?

  private def insert(batch: List[CardboardRow]) = {
    ctx.run(insertValues(batch))
  }

  override def create(cardboard: Cardboard): ZIO[Any, SQLException, List[Long]] = insert(List(cardboard))

  override def get: ZIO[Any, SQLException, List[CardboardRow]] = ctx.run(query[CardboardRow])

  override def update(cardboard: Cardboard): ZIO[Any, SQLException, CardboardRow] = ???

  override def delete(cardboard: Cardboard): ZIO[Any, SQLException, CardboardRow] = ??? // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}

object CardboardRepositoryDefault {

  // todo check collisions
  def create(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, List[Long]] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.create(cardboard))
  }

  def get: ZIO[CardboardRepositoryDefault, SQLException, List[CardboardRow]] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.get)
  }

  def update(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, CardboardRow] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.update(cardboard))
  }

  def delete(cardboard: Cardboard): ZIO[CardboardRepositoryDefault, SQLException, CardboardRow] = {
    ZIO.serviceWithZIO[CardboardRepositoryDefault](_.update(cardboard))
  }

  lazy val live: ZLayer[DBContext, Nothing, CardboardRepositoryDefault] = ZLayer.fromFunction(new CardboardRepositoryDefault(_))
}
