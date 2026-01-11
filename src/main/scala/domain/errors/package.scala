package irka.grilleEncoder
package domain

import domain.model.User
import infrastructure.db.entities.DBContext

import zio.json.*

package object errors {
  class InvalidJson(err: String) extends Exception(s"Invalid JSON: $err; try a JSON like ${User("1", "user1", List.empty).toJson}")
  class DbError(err: String) extends Exception(s"For db type: $DBContext")
}
