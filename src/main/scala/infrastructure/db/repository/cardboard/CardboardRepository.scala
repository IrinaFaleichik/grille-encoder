package irka.grilleEncoder
package infrastructure.db.repository.cardboard
import domain.model.Cardboard
import infrastructure.db.entities.{RowObject, DBContext}

import scala.language.implicitConversions

// shadows core.repository.CardboardRepository
trait CardboardRepository(ctx: DBContext) extends core.repository.CardboardRepository {

  // todo is it necessary to be implicit?
  implicit def toRow(cardboard: Cardboard): RowObject.Cardboard = RowObject.Cardboard(cardboard.id, cardboard.name, cardboard.userId)
  //  implicit def toDomain(row: CardboardRow, squares: List[Square]): Cardboard = Cardboard(row.id, row.name, squares, row.userId)
  // todo do I need implicit toDomain?
}
