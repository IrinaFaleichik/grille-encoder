package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.domain.model.{Cardboard, User}
import irka.grilleEncoder.infrastructure.db.entities.UserRow
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.IsSubtypeOfError.impl
import zio.ZIO

//Example interpreter for a domain repo
class UserRepositoryLive(ctx: Quill.Sqlite[SnakeCase]) {
  // Mapping logic lives here todo move to domain? find the whole user or better just return user id?
  def listCardboards(userId: String): List[Cardboard] = ???

  private def toRow(user: User): UserRow =
    UserRow(user.id, user.name)

  private def toDomain(row: UserRow, cardboards: List[Cardboard]): User =
    User(row.id, row.name, cardboards)

  import ctx.*

  def insertValues(users: List[UserRow]) = quote {
    liftQuery(users).foreach(usr => query[UserRow].insertValue(usr))
  }

  def create(user: List[UserRow]) = ctx.run(insertValues(user))

}
