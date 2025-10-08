package infrastructure.db

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.{ExitCode, URIO, ZIO, ZIOAppDefault, ZLayer}

import java.sql.SQLException

// TODO read this: https://zio.dev/zio-quill/writing-queries/
case class Students(id: Int, name: String)

//class MyContext extends SqlMirrorContext(MirrorSqlDialect, Literal)
//
//trait MySchema {
//
//  val ctx: MyContext
//  import ctx._
//  
//  val getStudents = ctx.run(query[Students])
//  def insertValues(circles: List[Students]) = quote {
//    liftQuery(circles).foreach(c => query[Students].insertValue(c))
//  }
//}

//val ctx = new SqlMirrorContext(SqliteDialect, SnakeCase)

class DataService(ctx: Quill.Sqlite[SnakeCase]) {

  import ctx._

  def getStudents: ZIO[Any, SQLException, List[Students]] = {
    ctx.run(query[Students])
  }

  def insertValues(circles: List[Students]) = quote {
    liftQuery(circles).foreach(c => query[Students].insertValue(c))
  }

  def insertStudents(batch: List[Students]): ZIO[Any, SQLException, List[Long]] = {
    ctx.run(insertValues(batch))
  }
}

object DataService {
  def getStudents: ZIO[DataService, SQLException, List[Students]] = {
    ZIO.serviceWithZIO[DataService](_.getStudents)
  }

  def insertStudents(batch: List[Students]): ZIO[DataService, SQLException, List[Long]] = {
    ZIO.serviceWithZIO[DataService](_.insertStudents(batch))
  }
  val live = ZLayer.fromFunction(new DataService(_))
}

// TODO: how to produce 2 operations in the same app?
object Main extends ZIOAppDefault {

  override def run: URIO[Any, ExitCode] = {
    val connection = DataService.live
    DataService.getStudents
      .provide(
        connection,
        Quill.Sqlite.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
      .exitCode

    // an insert operation
//    DataService.insertStudents(List(Students(3, "Vyacheslav")))
//      .provide(
//        connection,
//        Quill.Sqlite.fromNamingStrategy(SnakeCase),
//        Quill.DataSource.fromPrefix("myDatabaseConfig")
//      )
//      .debug("Results")
  }
}
