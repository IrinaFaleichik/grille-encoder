package irka.grilleEncoder
package infrastructure.db

import domain.model.{CardboardId, SquareId, UserId}

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill

// DB entities (what maps to tables)
package object entities {
  enum TableEntity {
    case Cardboard(id: CardboardId, name: String, userId: String)

    case Square(
                             id: SquareId,
                             cardboardId: CardboardId, // Foreign key from cardboard.id, required
                             startX: Int,
                             startY: Int,
                             endX: Int,
                             endY: Int
                           )

    case User(id: UserId, name: String)
  }
  type DBContext = Quill.Sqlite[SnakeCase]
  object DBContext {
    val namingStrategy: SnakeCase.type = SnakeCase
  }
}
