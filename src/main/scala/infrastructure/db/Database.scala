package infrastructure.db

// Connection pool & transactor setup
//class Database {
//
//}
// infrastructure/db/Database.scala

// DAO or Repository pattern
// https://habr.com/ru/articles/263033/

import zio._
import io.getquill._

object Database {

  // make as in ZIO guide: https://zio.dev/zio-quill/contexts/#quill-jdbc
  // Define a live connection pool layer
  val live: ZLayer[Any, Er, DBConnection] =
    DBConnection.mysql(
      host = "",
      port = ???,
      database = "",
      props = Map()//???
    )

  // Wrap the connection pool into a ZIO DB layer
  val jdbcLayer: ZLayer[Any, Throwable, DB] =
    live >>> DB.live
}
