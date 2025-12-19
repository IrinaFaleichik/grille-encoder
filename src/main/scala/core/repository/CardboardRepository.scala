package irka.grilleEncoder.core.repository

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import irka.grilleEncoder.domain.model
import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import irka.grilleEncoder.infrastructure.db.entities.{TableEntity, DBContext}
import zio.*

import java.sql.SQLException

trait CardboardRepository {

  def create(cardboard: Cardboard): ZIO[Any, SQLException, List[Long]]

  def get: ZIO[Any, SQLException, List[TableEntity.Cardboard]]

  def update(cardboard: Cardboard): Task[Cardboard]

  def delete(cardboard: Cardboard): Task[Cardboard] // todo return Cardboard or number of deleted cardboards + CHECK THE DELETE IN OBJECT

}
