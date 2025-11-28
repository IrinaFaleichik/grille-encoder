package irka.grilleEncoder.core.repository

import domain.model.Cardboard
import domain.model.User
import zio.*

trait CardboardRepository {
  def create(cardboard: Cardboard): Task[Cardboard]
  
  def findById(cardboard: Cardboard): Task[Option[Cardboard]]
  
  def findByUserId(cardboard: Cardboard): Task[List[Cardboard]]
  
  def update(cardboard: Cardboard): Task[Cardboard]
  
  def delete(cardboard: Cardboard): Task[Cardboard] // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT
  
  // Cascade delete when user is deleted (if not handled at DB level)
  def deleteByUser(user: User): Task[Unit]
}

// TODO implement it later, move to domain?
object CardboardRepository {
  def create(cardboard: Cardboard): ZIO[CardboardRepository, Throwable, Cardboard] = ???
  
  def findById(cardboardId: String): ZIO[CardboardRepository, Throwable, Option[Cardboard]] = ???
  
  def findByUserId(userId: String): ZIO[CardboardRepository, Throwable, List[Cardboard]] = ???
  
  def findAll: ZIO[CardboardRepository, Throwable, List[Cardboard]] = ???
  
  def update(cardboardId: String, name: String): ZIO[CardboardRepository, Throwable, Cardboard] = ???
  
  def delete(cardboardId: String): ZIO[CardboardRepository, Throwable, Unit] = ???
  
  def deleteByUserId(userId: String): ZIO[CardboardRepository, Throwable, Unit] = ???
}
