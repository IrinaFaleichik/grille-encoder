package irka.grilleEncoder
package core.repository

import domain.model.User
import zio.*

trait UserRepository {
  def create(user: User): Task[List[Long]]

  //  def findById(user: User): Task[Option[User]]

  def get: Task[List[User]]

  def update(user: User): Task[List[Long]]

  def delete(user: User): Task[List[Long]]
}
