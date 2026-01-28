package irka.grilleEncoder
package domain

import domain.model.User
import infrastructure.db.entities.DBContext

import zio.json.*

package object errors {
  //todo replace with zio.json.JsonError? but also add expected JSON format
  class InvalidJson(expectedJson: String)(err: String) extends Exception(s"Invalid JSON: $err; try a JSON like ${expectedJson.toJson}")

  class DbError(err: String) extends Exception(s"For db type: $DBContext")
}
