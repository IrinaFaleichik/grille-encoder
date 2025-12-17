package irka.grilleEncoder
package infrastructure.db.repository.square

import domain.model.{Cardboard, Point, Square}
import infrastructure.db.entities.{DBContext, RowObject}

import scala.language.implicitConversions

// shadows core.repository.SquareRepository
trait SquareRepository extends core.repository.SquareRepository {

  def findCardboardById(cardboardId: String): Cardboard = ???
  
  def toRow(square: Square): RowObject.Square = RowObject.Square(square.id, square.cardboard.id, square.start.x, square.start.y, square.end.x, square.end.y)
  def toDomain(square: RowObject.Square): Square = Square(square.id, Point(square.startX, square.startY), Point(square.endX, square.endY), findCardboardById(square.cardboardId))

}
