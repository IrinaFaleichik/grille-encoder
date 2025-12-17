package irka.grilleEncoder.core.repository

import irka.grilleEncoder.domain.model.Square
import irka.grilleEncoder.domain.model.Cardboard
import irka.grilleEncoder.infrastructure.db.entities.{DBContext, RowObject}
import zio.*

trait SquareRepository {
  def create(square: Square): Task[List[Long]]

  def get: Task[List[Square]]
  // todo delete method?
//  def findByCompositeKey(cardboard: Cardboard, square: Square): Task[Option[Square]]
//
//  def findByCardboardId(cardboardId: String): Task[List[Square]]

//  def findAll: Task[List[Square]]
  
  def update(square: Square): Task[Square]
  
  def delete(square: Square): Task[Square] // todo return Square or number of deleted squares + CHECK THE DELETE IN OBJECT
  
  // Cascade delete when cardboard is deleted (if not handled at DB level)
//  def deleteByCardboardId(cardboardId: String): Task[Unit]
}
