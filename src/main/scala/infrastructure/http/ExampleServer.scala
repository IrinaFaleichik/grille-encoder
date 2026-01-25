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
import infrastructure.logging.Logger

import application.api.auth.AuthService
import infrastructure.db.repository.auth.{AuthRepositoryByEmail, AuthRepositoryByUsername}

import javax.sql.DataSource

object ExampleServer extends ZIOAppDefault {

  lazy val ctx: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]] = Quill.Sqlite.fromNamingStrategy(entities.DBContext.namingStrategy) // context to write queries
  lazy val con: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromPrefix("myDatabaseConfig")

  //todo move to zio-config?
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Logger.live
  val appLayerAuth: ZLayer[Any, Throwable, AuthService] =
    con >>> ctx >>>
      AuthRepositoryByUsername.live ++
        AuthRepositoryByEmail.live >>>
      AuthService.live
  val appLayer: ZLayer[Any, Throwable, UserRepositoryDefault & AuthService] =
    (con >>> ctx >>> UserRepositoryDefault.live) ++ appLayerAuth

  // Serving the routes using the default server layer on port 8080
  def run: ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Logging started")
    _ <- Server.serve(AppRoutes.routes).provide(Server.defaultWithPort(8083), appLayer)
  } yield ()
}
