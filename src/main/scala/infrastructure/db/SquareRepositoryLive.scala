package irka.grilleEncoder
package infrastructure.db

import domain.model.{Cardboard, CardboardId, Point, Square, User}
import infrastructure.db.entities.{CardboardRow, SquareRow}

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.ZIO

import java.sql.SQLException

class SquareRepositoryLive(ctx: Quill.Sqlite[SnakeCase]) {
  // Mapping logic lives here todo move to domain? find the whole user or better just return user id?
  def findCardboard(cardboardId: CardboardId): Cardboard = ???

  private def toRow(square: Square): SquareRow =
    SquareRow(
      square.id,
      square.cardboard.id,
      square.start.x,// todo unapply untuple?
      square.start.y,
      square.end.x,// todo unapply untuple?
      square.end.y
    )

  private def toDomain(row: SquareRow): Square =
    Square(row.id, Point(row.startX, row.startY), Point(row.endX, row.endY), findCardboard(row.cardboardId))

  import ctx.*

  def insertValues(square: List[SquareRow]) = quote {
    liftQuery(square).foreach(sqr => query[SquareRow].insertValue(sqr))
  }

  def create(square: List[SquareRow]) = ctx.run(insertValues(square))

}
