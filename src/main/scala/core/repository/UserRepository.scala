package irka.grilleEncoder.core.repository

import irka.grilleEncoder.domain.model.User
import irka.grilleEncoder.infrastructure.db.entities.{DBContext, RowObject}
import zio.*

trait UserRepository {
  def create(user: User): Task[List[Long]]
  
//  def findById(user: User): Task[Option[User]]
  
  def get: Task[List[User]]
  
  def update(user: User): Task[User]
  
  def delete(user: User): Task[User] // todo return User or number of deleted users + CHECK THE DELETE IN OBJECT
}
