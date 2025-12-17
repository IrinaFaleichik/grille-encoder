package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, RowObject}

import scala.language.implicitConversions

// shadows core.repository.UserRepository
trait UserRepository extends core.repository.UserRepository {

  def toRow(user: User): RowObject.User = RowObject.User(user.id, user.name)
  def toDomain(user: RowObject.User): User = User(user.id, user.name, Nil)

}
