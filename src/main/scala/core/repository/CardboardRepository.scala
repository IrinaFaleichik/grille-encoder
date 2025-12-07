package irka.grilleEncoder.core.repository

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import irka.grilleEncoder.domain.model
import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import irka.grilleEncoder.infrastructure.db.{CardboardRepositoryLive, DBService}
import irka.grilleEncoder.infrastructure.db.entities.{CardboardRow, DBContext}
import zio.*

import scala.language.implicitConversions

trait CardboardRepository(DBService: DBService) {

  private def findUser(userId: String): User = ???
  //    def countPicturesOf(topic: String): ZIO[Any, Nothing, Int]

  def create(cardboard: Cardboard): Task[Cardboard]

  def get(cardboard: Cardboard): Task[List[Cardboard]]

  def update(cardboard: Cardboard): Task[Cardboard]

  def delete(cardboard: Cardboard): Task[Cardboard] // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}
//
//  def countPicturesOf(topic: String): ZIO[CardboardRepository, Nothing, Int] =
//    ZIO.environmentWithZIO(_.get.countPicturesOf(topic))


//class CardboardRepository1(ctx: Quill.Sqlite[SnakeCase]) {
//
//  def create(cardboard: Cardboard): Task[Cardboard]
//
//  def get(cardboard: Cardboard): Task[Option[Cardboard]]
//
//  // todo move to class by id/byUser
//  def findById(cardboard: Cardboard): Task[Option[Cardboard]]
//  // todo move to class by id/byUser
//  def findByUserId(cardboard: Cardboard): Task[List[Cardboard]]
//
//  def update(cardboard: Cardboard): Task[Cardboard]
//
//  def delete(cardboard: Cardboard): Task[Cardboard] // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT
//
//  // todo move to class ByUser Cascade delete when user is deleted (if not handled at DB level)
//  def deleteByUser(user: User): Task[Unit]
//
//  def live[CRImpl <: CardboardRepository1]: ZLayer[DBContext, Nothing, CRImpl] = {
//    ZLayer.fromFunction(new CRImpl(_))
//  }
//}

//final class CardboardRepositoryDefault {
//
//}

// TODO implement it later, move to domain?
//object CardboardRepository {
//  def create(cardboard: Cardboard): ZIO[CardboardRepository, Throwable, Cardboard] = CardboardRepositoryLive.create(cardboard)
//
////  def findById(cardboardId: String): ZIO[CardboardRepository, Throwable, Option[Cardboard]] = ???
////
////  def findByUserId(userId: String): ZIO[CardboardRepository, Throwable, List[Cardboard]] = ???
////
//  def get: ZIO[CardboardRepository, Throwable, List[Cardboard]] = ???
//
//  def update(cardboardId: String, name: String): ZIO[CardboardRepository, Throwable, Cardboard] = ???
//
//  def delete(cardboardId: String): ZIO[CardboardRepository, Throwable, Unit] = ???
////
////  def deleteByUserId(userId: String): ZIO[CardboardRepository, Throwable, Unit] = ???
//
//  ???
//  lazy val live: CardboardRepository => ZLayer[Quill.Sqlite[SnakeCase], Nothing, CardboardRepository] = { from =>
//    ZLayer.fromFunction(from)
//  }
//  val live: ZLayer[Quill.Sqlite[SnakeCase], Nothing, CardboardRepositoryLive] = ZLayer.fromFunction(new CardboardRepositoryLive(_))
//}
