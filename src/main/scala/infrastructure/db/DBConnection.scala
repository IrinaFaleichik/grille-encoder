package irka.grilleEncoder
package infrastructure.db

package irka.grilleEncoder.infrastructure.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.jdbczio.Quill
import io.getquill.{SnakeCase, SqliteDialect}
import zio.{IO, Task, ULayer, ZIO, ZLayer}

import java.sql.{Connection, SQLException}
import java.util.Properties
import javax.sql.DataSource

object DBConnection {
  // Load config (assuming you have ZIOConfig or similar for loading application.conf)

  val DBNamingStrategy = SnakeCase
  type DBNamingStrategy = SnakeCase
  type DBDriver = Quill.Sqlite[DBNamingStrategy]

  private def loadConfig: Task[HikariConfig] = ZIO.attempt {
    val configProps = new Properties()
    // TODO: Load from application.conf or ZIOConfig; hardcoded for illustration
    configProps.setProperty("dataSourceClassName", "org.sqlite.JDBC")
    configProps.setProperty("dataSource.url", "jdbc:sqlite:/path/to/your/db.sqlite") // Replace with actual DB URL
    configProps.setProperty("maximumPoolSize", "10")
    new HikariConfig(configProps)
  }

  // Create the data source as a ZIO effect
//  ctx: Quill.Sqlite[SnakeCase]
  private def createDataSource: Task[HikariDataSource] = for {
    config <- loadConfig
  } yield new HikariDataSource(config)

  // Provide a Quill context layer using the data source
  //todo with DBDriver type
  val live: ZLayer[Any, Throwable, ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase.type]]] = ZLayer {
    createDataSource.map { ds =>
      Quill.Sqlite.fromNamingStrategy(DBNamingStrategy)
    }
  }

  // Utility to get a single connection (for manual use, if needed)
//  def getConnection: IO[SQLException, Connection] = createDataSource.flatMap { ds =>
//    ZIO.attempt(ds.getConnection()).refineToOrDie[SQLException]
//  }
}