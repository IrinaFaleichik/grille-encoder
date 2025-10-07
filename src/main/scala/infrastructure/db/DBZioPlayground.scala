package infrastructure.db

import com.typesafe.config.ConfigFactory
import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZIOAppDefault, ZLayer}

import java.sql.SQLException

//TODO need some experiments with insertion to the db
case class Students(id: Int, name: String)

//class MyContext extends SqlMirrorContext(MirrorSqlDialect, Literal)
//
//trait MySchema {
//
//  val c: MyContext
//  import c._
//
//  val people = quote {
//    querySchema[Student]("students")
//  }
//}
//val ctx = new SqlMirrorContext(PostgresDialect, SnakeCase)

class DataService(ctx: Quill.Sqlite[SnakeCase]) {

  import ctx._

  def getStudents: ZIO[Any, SQLException, List[Students]] = {
    ctx.run(query[Students])
  }
  
//  ctx.run(insertValues(List(Students(2, "Alice"))))

//  def insertStudents: ZIO[Any, SQLException, List[Students]] = {
//    ctx.run(query[Students].add())
//  }
}

object DataService {
  def getStudents: ZIO[DataService, SQLException, List[Students]] = {
    ZIO.serviceWithZIO[DataService](_.getStudents)
  }

//  def insertStudents = {//: ZIO[DataService, SQLException, List[Students]] = {
//    ZIO.serviceWithZIO[DataService](_.insertStudents(stud))
//  }
//}
  val live = ZLayer.fromFunction(new DataService(_))
}

object Main extends ZIOAppDefault {

  override def run = {
    DataService.getStudents
      .provide(
        DataService.live,
        Quill.Sqlite.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
      .exitCode
  }
}
