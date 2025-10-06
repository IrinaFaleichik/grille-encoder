//package infrastructure.db
//
//// TODO create a local DB please
//// TODO connect to the local DB
//// TODO 
//// Connection pool & transactor setup
////class Database {
////
////}
//// infrastructure/db/Database.scala
//
//// TODO DAO or Repository pattern
//// https://habr.com/ru/articles/263033/
//// TODO ZIO example for DB lib https://github.com/deusaquilus/zio-quill-gettingstarted
//
//import zio._
//import io.getquill._
//
//object Database {
//
//  // make as in ZIO guide: https://zio.dev/zio-quill/contexts/#quill-jdbc
//  // Define a live connection pool layer
//  val live: ZLayer[Any, Er, DBConnection] =
//    DBConnection.mysql(
//      host = "",
//      port = ???,
//      database = "",
//      props = Map()//???
//    )
//
//  // Wrap the connection pool into a ZIO DB layer
//  val jdbcLayer: ZLayer[Any, Throwable, DB] =
//    live >>> DB.live
//}
