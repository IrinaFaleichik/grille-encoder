package irka.grilleEncoder.infrastructure.db

import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import irka.grilleEncoder.infrastructure.db.entities.DBContext
import zio.{Scope, ZLayer}

import javax.sql
import javax.sql.DataSource

// todo inject ctx and con - could use for different db connections
trait Database {
//  def ctx: ZLayer[sql.DataSource, Nothing, Quill.Sqlite[T]] = Quill.Sqlite.fromNamingStrategy[T] // context to write queries
  lazy val ctx: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]] = Quill.Sqlite.fromNamingStrategy(entities.DBContext.namingStrategy)
  lazy val con: ZLayer[Any, Throwable, sql.DataSource] = Quill.DataSource.fromPrefix("myDatabaseConfig")
  lazy val content: ZLayer[Nothing, Throwable, DataSource & Quill.Sqlite[SnakeCase.type]] = ctx ++ con
}

object Database {

  // 2) Wire up the live layer
  //    here configure JDBC URL, driver, pool, etc.
  lazy val live: ZLayer[Any, Throwable, Database] = ???//{
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
  def content(db: Database): ZLayer[Nothing, Throwable, DataSource & Quill.Sqlite[SnakeCase.type]] = {
    db.con ++ db.ctx
  }

  // 3) Helper to access the service
  //  def getContext =
  //    zio.ZIO.service[Database].map(_.quill)
}