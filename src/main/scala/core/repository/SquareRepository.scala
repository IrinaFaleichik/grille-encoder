package irka.grilleEncoder.core.repository

import domain.model.Square
import domain.model.Cardboard
import zio.*

trait SquareRepository {
  def create(square: Square): Task[Square]

  // todo delete method?
  def findByCompositeKey(cardboard: Cardboard, square: Square): Task[Option[Square]]

  def findByCardboardId(cardboardId: String): Task[List[Square]]

  def findAll: Task[List[Square]]
  
  def update(square: Square): Task[Square]
  
  def delete(square: Square): Task[Unit] // todo return Square or number of deleted squares + CHECK THE DELETE IN OBJECT
  
  // Cascade delete when cardboard is deleted (if not handled at DB level)
  def deleteByCardboardId(cardboardId: String): Task[Unit]
}

object SquareRepository {
  def create(): ZIO[SquareRepository, Throwable, Square] = ???
  
  def findByCompositeKey(cardboardId: String, squareId: Int): ZIO[SquareRepository, Throwable, Option[Square]] = ???
  
  def findByCardboardId(cardboardId: String): ZIO[SquareRepository, Throwable, List[Square]] = ???
  
  def findAll: ZIO[SquareRepository, Throwable, List[Square]] = ???
  
  def update(square: Square): ZIO[SquareRepository, Throwable, Square] = ???
  
  def delete(cardboardId: String, squareId: Int): ZIO[SquareRepository, Throwable, Unit] = ???
  
  def deleteByCardboardId(cardboardId: String): ZIO[SquareRepository, Throwable, Unit] = ???
}
