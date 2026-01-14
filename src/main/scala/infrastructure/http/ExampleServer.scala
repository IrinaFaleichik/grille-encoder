package irka.grilleEncoder
package infrastructure.http

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import application.api.AppRoutes
import zio.*
import zio.http.*
import infrastructure.db
import infrastructure.db.entities
import infrastructure.db.repository.user.UserRepositoryDefault

import javax.sql.DataSource

object ExampleServer extends ZIOAppDefault {

  lazy val ctx: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]] = Quill.Sqlite.fromNamingStrategy(entities.DBContext.namingStrategy) // context to write queries
  lazy val con: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromPrefix("myDatabaseConfig")

  val appLayer: ZLayer[Any, Throwable, UserRepositoryDefault] = con >>> ctx >>> UserRepositoryDefault.live

  // Serving the routes using the default server layer on port 8080
  def run: ZIO[Any, Throwable, Unit] = for {
    _ <- Server.serve(AppRoutes.routes).provide(Server.defaultWithPort(8083), appLayer)
  } yield ()
}
