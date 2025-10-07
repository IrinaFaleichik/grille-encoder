package infrastructure.db

import com.typesafe.config.ConfigFactory
import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.util.LoadConfig.getClass
import zio.{ZIO, ZIOAppDefault, ZLayer}

import java.sql.SQLException

case class Student(name: String)

class MyContext extends SqlMirrorContext(MirrorSqlDialect, Literal)
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

//object DBZioPlayground {
//    class DataService(quill: Quill.Postgres[SnakeCase]) {
class DataService(ctx: Quill.Sqlite[SnakeCase]) {

  import ctx._

  private def getStudentsQuery: Quoted[Query[Student]] = {

    val students: Quoted[Query[Student]] = quote {
      query[Student]
    }
    students
  }

  private def getStudents: ZIO[Any, SQLException, List[Student]] = ctx.run(getStudentsQuery)
}

object DataService {
  def getStudents =
    ZIO.serviceWithZIO[DataService](_.getStudents)

  val live = ZLayer.fromFunction(new DataService(_))
}


/**
 * Simple example of Quill using the jdbc-zio context
 */
object Main extends ZIOAppDefault {

  //  import DBZioPlayground._

  override def run = {
    DataService.getStudents
      .provide(
        DataService.live,
        Quill.Sqlite.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
//      ("Results")
      .exitCode
  }
}
