package infrastructure.db

// TODO unsafe + no concurrency + IDK how to specify the path

import java.sql.{Connection, DriverManager, SQLException, Statement}

object DBPlayground extends App {

  private def newConnection(): Either[SQLException, Connection] = {
    val url: String = "jdbc:sqlite:cardboardPool.db" // TODO how to specify database path?

    try {
      val res = Right(DriverManager.getConnection(url))
      println("Connection Successful")
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
