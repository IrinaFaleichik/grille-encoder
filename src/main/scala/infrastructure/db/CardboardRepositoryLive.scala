package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import irka.grilleEncoder.infrastructure.db.entities.CardboardRow
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.ZIO
import io.getquill.*

import java.sql.SQLException
import zio.IsSubtypeOfError.impl

class CardboardRepositoryLive(ctx: Quill.Sqlite[SnakeCase]) {
  // Mapping logic lives here todo move to domain? find the whole user or better just return user id?
  def findUser(userId: String): User = ???

  private def toRow(cardboard: Cardboard): CardboardRow =
    CardboardRow(cardboard.id, cardboard.name, cardboard.user.id)

  private def toDomain(row: CardboardRow, squares: List[Square]): Cardboard =
    Cardboard(row.id, row.name, squares, findUser(row.userId))

  import ctx.*

  def insertValues(cardboards: List[CardboardRow]) = quote {
    liftQuery(cardboards).foreach(crd => query[CardboardRow].insertValue(crd))
  }

  def create(cardboard: List[CardboardRow]) = ctx.run(insertValues(cardboard))

}
