package irka.grilleEncoder
package infrastructure.db.repository.square

import domain.model.Square
import infrastructure.db.entities.{SquareRow, DBContext}

import scala.language.implicitConversions

// shadows core.repository.SquareRepository
trait SquareRepository extends core.repository.SquareRepository {

  def toRow(square: Square): SquareRow = SquareRow(square.id, square.cardboard.id, square.start.x, square.start.y, square.end.x, square.end.y)

}
