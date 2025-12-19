package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, TableEntity}

import scala.language.implicitConversions

// shadows core.repository.UserRepository
trait UserRepository extends core.repository.UserRepository {

  def toRow(user: User): TableEntity.User = TableEntity.User(user.id, user.name)
  def toDomain(user: TableEntity.User): User = User(user.id, user.name, Nil)

}
