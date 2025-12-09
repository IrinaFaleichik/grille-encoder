package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.infrastructure.db.entities.{CardboardRow, DBContext, RowObject}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.jdbczio.Quill
import io.getquill.{SnakeCase, SqliteDialect}
import zio.{IO, Task, ULayer, ZIO, ZLayer}

import java.sql.{Connection, SQLException}
import java.util.Properties
import javax.sql.DataSource
import scala.util.Try
import irka.grilleEncoder.infrastructure.db.repository.cardboard.CardboardRepositoryDefault

object DBService {
  lazy val cardboardRepositoryDefault = CardboardRepositoryDefault.live // repository obj
  lazy val ctx = Quill.Sqlite.fromNamingStrategy(SnakeCase) // context to write queries
  lazy val con = Quill.DataSource.fromPrefix("myDatabaseConfig")

  def getCardboards: Task[List[CardboardRow]] =
    CardboardRepositoryDefault.get
      .provide(
        cardboardRepositoryDefault,
        ctx,
        con
      )
}
