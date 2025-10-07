package infrastructure.db

// TODO unsafe + no concurrency + IDK how to specify the path

import java.sql.{Connection, DriverManager, SQLException, Statement}
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.util.Properties

object DBPlayground extends App {

  val config = ConfigFactory.load()
  val dbUrl = config.getString("database.source.url")
//  val dbUrl = config.getString("myDatabaseConfig")
  val hikariConfig1 = config.getConfig("myDatabaseConfig")

  def configProperties = {
    import scala.jdk.CollectionConverters._
    val p = new Properties
    for (entry <- hikariConfig1.entrySet.asScala) {
      p.setProperty(entry.getKey, entry.getValue.unwrapped.toString)
      println(s"${entry.getKey}, ${entry.getValue.unwrapped.toString}")
    }

    p
  }

  private def newConnection(): Either[SQLException, Connection] = {
    val url: String = dbUrl // TODO how to specify database path?

    try {
      val res = Right(DriverManager.getConnection(url))
      println("Connection Successful")
      val res1 = Right((new HikariDataSource(new HikariConfig(configProperties))).getConnection)

      res
    } catch {
      case e: SQLException => println("Error Connecting to Database")
        e.printStackTrace()
        Left(e)
    }
  }

  private def closeConnection(connection: Either[SQLException, Connection]): Unit = {

    try {
      connection match {
        case Right(c) =>
          c.close()
          System.out.println("Connection Closed")
        case _ => // TODO kinda error?
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  private def main(): Unit = {
    val connection: Either[SQLException, Connection] = newConnection()

    val statement: Either[SQLException, Statement] = connection.map(e => e.createStatement())

    val createTable = "CREATE TABLE IF NOT EXISTS students(id INTEGER PRIMARY KEY, name TEXT)"
    statement.map(_.executeUpdate(createTable))

    val addJohn = "INSERT INTO students(name) VALUES(‘John’)"
    val addAlice = "INSERT INTO students(name) VALUES(‘Alice’)"

    statement.map(_.executeUpdate(addJohn))
    statement.map(_.executeUpdate(addAlice))

    println("Data Inserted")

    statement.map(_.close())
    closeConnection(connection)
  }

  main()
}
