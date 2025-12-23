package irka.grilleEncoder.infrastructure.db

import io.getquill.context.qzio.ZioContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.idiom.Idiom
import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase, SqliteDialect, autoQuote}
import irka.grilleEncoder.infrastructure.db.entities.DBContext
import zio.{Scope, ZIO, ZLayer}

import javax.sql
import javax.sql.DataSource

// todo inject ctx and con - could use for different db connections

trait Database[DBType, Dialect <: Idiom, DBName <: ZioContext[Dialect, NamingStrategy], NamingStrategy <: io.getquill.NamingStrategy] {
  val ctx: DBName
  val con: DBType
  //  lazy val content: DataSource & DBName
}

trait DatabaseSQL[Dialect <: SqlIdiom, DBName <: Quill[Dialect, NamingStrategy], NamingStrategy <: io.getquill.NamingStrategy]
  extends Database[sql.DataSource, Dialect, DBName, NamingStrategy]

// "myDatabaseConfig"
// todo inject naming strategy? through trait or through generic?
final class SQLiteDatabase(override val con: sql.DataSource,
                           override val ctx: Quill.Sqlite[SnakeCase.type])
  extends DatabaseSQL

// todo inject all or implement here?
object SQLiteDatabase {

  // 2) Wire up the live layer
  //    here configure JDBC URL, driver, pool, etc.

  //  def init: ZLayer[String, Throwable, SQLiteDatabase] = {}

  lazy val live: ZLayer[DataSource & Quill.Sqlite[SnakeCase.type], Throwable, SQLiteDatabase] = {

    ZLayer.fromFunction { (ds: sql.DataSource, ctx: Quill.Sqlite[SnakeCase.type]) =>
      new SQLiteDatabase(ds, ctx)
    }
  }

//  def make(prefix: String, ds: sql.DataSource, ctx: Quill.Sqlite[SnakeCase.type]): SQLiteDatabase = {
//    new SQLiteDatabase(prefix, ds, ctx)
//  }

}
