package irka.grilleEncoder.infrastructure.db

import io.getquill.context.qzio.ZioContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.idiom.Idiom
import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase, SqliteDialect}
import irka.grilleEncoder.infrastructure.db.entities.DBContext
import zio.{Scope, ZLayer}

import javax.sql
import javax.sql.DataSource

// todo inject ctx and con - could use for different db connections

trait Database[DBType, Dialect <: Idiom, DBName <: ZioContext[Dialect, NamingStrategy], NamingStrategy <: io.getquill.NamingStrategy] {
  lazy val ctx: ZLayer[DataSource, Nothing, DBName]
  lazy val con: ZLayer[Any, Throwable, DBType]
  lazy val content: ZLayer[Nothing, Throwable, DataSource & DBName]
}

trait DatabaseSQL[Dialect <: SqlIdiom, DBName <: Quill[Dialect, NamingStrategy], NamingStrategy <: io.getquill.NamingStrategy] extends Database[sql.DataSource, Dialect, DBName, NamingStrategy]// {
//  lazy val ctx: ZLayer[DataSource, Nothing, DBName]
//  lazy val con: ZLayer[Any, Throwable, sql.DataSource]
//  lazy val content: ZLayer[Nothing, Throwable, DataSource & DBName]
//}

// "myDatabaseConfig"
// todo inject naming strategy? through trait or through generic?
final class SQLiteDatabase(dbConfig: String) extends DatabaseSQL {//[Quill.Sqlite[SnakeCase.type], SqliteDialect, SnakeCase.type] {
  lazy val ctx: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]] = Quill.Sqlite.fromNamingStrategy(SnakeCase)
  lazy val con: ZLayer[Any, Throwable, sql.DataSource] = Quill.DataSource.fromPrefix(dbConfig)
  lazy val content: ZLayer[Nothing, Throwable, DataSource & Quill.Sqlite[SnakeCase.type]] = ctx ++ con
}

object SQLiteDatabase {

  // 2) Wire up the live layer
  //    here configure JDBC URL, driver, pool, etc.
  lazy val live: ZLayer[Any, Throwable, SQLiteDatabase] = ???//{
//    ZLayer.fromFunction {
//      new Database {
//        override lazy val ctx: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]] = Quill.Sqlite.fromNamingStrategy(entities.DBContext.namingStrategy)
//        override lazy val con: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromPrefix("myDatabaseConfig")
//      }
//    }
    //    Quill
    //    .live[entities.DBContext.namingStrategy.type](
    //      dataSourceName = "myDatabaseConfig", // matches application.conf
    //      namingStrategy  = entities.DBContext.namingStrategy
    //    )
    //    .map(ctx =>
    //      new DataSource {
    //        val quill = ctx
    //      }
    //    )
//  }

  //  def unapply(): Unit = {
  //    
  //  }
  def content(db: SQLiteDatabase): ZLayer[Nothing, Throwable, DataSource & Quill.Sqlite[SnakeCase.type]] = {
    db.con ++ db.ctx
  }

  // 3) Helper to access the service
  //  def getContext =
  //    zio.ZIO.service[Database].map(_.quill)
}