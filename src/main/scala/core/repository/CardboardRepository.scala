package irka.grilleEncoder.core.repository

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import irka.grilleEncoder.domain.model
import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import irka.grilleEncoder.infrastructure.db.DBService
import irka.grilleEncoder.infrastructure.db.entities.{CardboardRow, DBContext}
import zio.*

trait CardboardRepository {

  def create(cardboard: Cardboard): Task[Cardboard]

  def get: ZIO[CardboardRepository, Throwable, List[Cardboard]]

  def update(cardboard: Cardboard): Task[Cardboard]

  def delete(cardboard: Cardboard): Task[Cardboard] // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}
