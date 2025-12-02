package irka.grilleEncoder.infrastructure.db

// DB entities (what maps to tables)
package object entities {

  case class CardboardRow(id: String, name: String, userId: String)

  case class SquareRow(
                        id: String,
                        cardboardId: Long, // Foreign key from cardboard.id, required
                        startX: Int,
                        startY: Int,
                        endX: Int,
                        endY: Int
                      )
  
  case class UserRow(id: String, name: String)
}
