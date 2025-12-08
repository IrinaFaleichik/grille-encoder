package irka.grilleEncoder.infrastructure.db

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import irka.grilleEncoder.domain.model.{CardboardId, SquareId, UserId}

// DB entities (what maps to tables)
package object entities {
  sealed trait RowObject

  final case class CardboardRow(id: CardboardId, name: String, userId: String) extends RowObject

  final case class SquareRow(
                        id: SquareId,
                        cardboardId: CardboardId, // Foreign key from cardboard.id, required
                        startX: Int,
                        startY: Int,
                        endX: Int,
                        endY: Int
                      ) extends RowObject
  
  final case class UserRow(id: UserId, name: String) extends RowObject

  type DBContext = Quill.Sqlite[SnakeCase]
  object DBContext {
    val namingStrategy = SnakeCase
  }
}
