package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.domain.model.{CardboardId, SquareId, UserId}

// DB entities (what maps to tables)
package object entities {

  final case class CardboardRow(id: CardboardId, name: String, userId: String)

  final case class SquareRow(
                        id: SquareId,
                        cardboardId: CardboardId, // Foreign key from cardboard.id, required
                        startX: Int,
                        startY: Int,
                        endX: Int,
                        endY: Int
                      )
  
  final case class UserRow(id: UserId, name: String)
}
