package irka.grilleEncoder
package infrastructure.db.repository.cardboard

import core.repository.*
import domain.model.{Cardboard, Square, User}
import infrastructure.db.entities.{CardboardRow, DBContext}
import infrastructure.db.{DBService, DataService, Students}

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.{Task, ZIO, ZLayer}

import java.sql.SQLException
import scala.language.implicitConversions

sealed trait CardboardRepositoryCommon(ctx: DBContext) extends CardboardRepository {

  // todo is it necessary to be implicit?
  implicit def toRow(cardboard: Cardboard): CardboardRow = CardboardRow(cardboard.id, cardboard.name, cardboard.userId)
//  implicit def toDomain(row: CardboardRow, squares: List[Square]): Cardboard = Cardboard(row.id, row.name, squares, row.userId)
// todo do I need implicit toDomain?
}

final class CardboardRepositoryDefault(ctx: DBContext) extends CardboardRepositoryCommon(ctx) {

  import ctx.*

  private def create(cardboard: Cardboard): ZIO[Any, SQLException, List[Long]] = insert(List(cardboard))

  override def get: ZIO[Any, SQLException, List[CardboardRow]] = ctx.run(query[CardboardRow])
//  def runQuery: ZIO[DBService, SQLException, List[CardboardRow]] = ctx.run(query[CardboardRow])

  override def insertValues(values: List[CardboardRow]) = quote {
    liftQuery(values).foreach(v => query[CardboardRow].insertValue(v))
  } // todo make function for 1 value insertion or not?

  override def insert(batch: List[CardboardRow]) = {
    ctx.run(insertValues(batch))
  }

  override def update(cardboard: Cardboard): ZIO[Any, SQLException, CardboardRow] = ???

  override def delete(cardboard: Cardboard): ZIO[Any, SQLException, CardboardRow] = ??? // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}

object CardboardRepositoryDefault {

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
