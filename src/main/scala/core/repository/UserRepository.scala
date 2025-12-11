package irka.grilleEncoder.core.repository

import irka.grilleEncoder.domain.model.User
import irka.grilleEncoder.infrastructure.db.entities.{DBContext, UserRow}
import zio.*

trait UserRepository {
  def create(user: User): Task[List[Long]]
  
//  def findById(user: User): Task[Option[User]]
  
  def get: Task[List[UserRow]]
  
  def update(user: User): Task[UserRow]
  
  def delete(user: User): Task[UserRow] // todo return User or number of deleted users + CHECK THE DELETE IN OBJECT
}
