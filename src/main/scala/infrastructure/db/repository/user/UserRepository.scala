package irka.grilleEncoder
package infrastructure.db.repository.user

import domain.model.User
import infrastructure.db.entities.{DBContext, UserRow}

import scala.language.implicitConversions

// shadows core.repository.UserRepository
trait UserRepository extends core.repository.UserRepository {

  def toRow(user: User): UserRow = UserRow(user.id, user.name)

}
