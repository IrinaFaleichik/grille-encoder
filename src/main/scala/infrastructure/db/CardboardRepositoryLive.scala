package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import irka.grilleEncoder.infrastructure.db.entities.{CardboardRow, DBContext}
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{Task, ZIO, ZLayer}
import io.getquill.*
import irka.grilleEncoder.core.repository.*

import java.sql.SQLException
import zio.IsSubtypeOfError.impl

import scala.language.implicitConversions

object CardboardRepositoryLive { //todo think please how to do or redo dummy and explicit type

  lazy val live: ZLayer[DBService, Throwable, CardboardRepository] = ZLayer.fromFunction(makeDummy)
  lazy val makeDummy: DBService => CardboardRepository= { dbService =>
    CardboardRepositoryDefault(dbService)
  }
}

//sealed trait CardboardRepositoryLive {
//
//}

final class CardboardRepositoryDefault(dbService: DBService) extends CardboardRepository(dbService) {
  // Mapping logic lives here todo move to domain? find the whole user or better just return user id?
  given toRow(using cardboard: Cardboard): CardboardRow =
  CardboardRow(cardboard.id, cardboard.name, cardboard.user.id)

  given toDomain(using row: CardboardRow, squares: List[Square]): Cardboard = Cardboard(row.id, row.name, squares, findUser(row.userId))

  //todo fix implicits - move them to CardboardRepository
//  using CardboardRepository.
  override def create(cardboard: Cardboard): Task[List[Long]] = dbService.insert[CardboardRow](List(cardboard))

  override def get: Task[List[Cardboard]] = dbService.get[Cardboard]
//  override def get(cardboard: Cardboard): Task[Option[Cardboard]] = DBService.get[Cardboard]

  override def update(cardboard: Cardboard): Task[Cardboard] = ???

  override def delete(cardboard: Cardboard): Task[Cardboard] = ??? // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

  def findUser(userId: String): User = ???

}
