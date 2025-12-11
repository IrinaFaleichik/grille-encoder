package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.infrastructure.db.entities.{CardboardRow, DBContext, RowObject, SquareRow, UserRow}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.jdbczio.Quill
import io.getquill.{SnakeCase, SqliteDialect}
import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import zio.{IO, Task, ULayer, ZIO, ZLayer}

import java.sql.{Connection, SQLException}
import java.util.Properties
import javax.sql.DataSource
import scala.util.Try
import irka.grilleEncoder.infrastructure.db.repository.cardboard.CardboardRepositoryDefault
import irka.grilleEncoder.infrastructure.db.repository.square.SquareRepositoryDefault
import irka.grilleEncoder.infrastructure.db.repository.user.UserRepositoryDefault

// todo too much code duplication, refactor
object Api {
  lazy val cardboardRepositoryDefault = CardboardRepositoryDefault.live // repository obj
  lazy val userRepositoryDefault = UserRepositoryDefault.live // repository obj
  lazy val squareRepositoryDefault = SquareRepositoryDefault.live // repository obj
  lazy val ctx = Quill.Sqlite.fromNamingStrategy(SnakeCase) // context to write queries
  lazy val con = Quill.DataSource.fromPrefix("myDatabaseConfig")

  /* Cardboard */
  def getCardboards: Task[List[CardboardRow]] =
    CardboardRepositoryDefault.get
      .provide(cardboardRepositoryDefault, ctx, con)

  def createCardboard(cardboard: Cardboard): Task[List[Long]] =
    CardboardRepositoryDefault.create(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  def updateCardboard(cardboard: Cardboard): Task[CardboardRow] =
    CardboardRepositoryDefault.update(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  def deleteCardboard(cardboard: Cardboard): Task[CardboardRow] =
    CardboardRepositoryDefault.delete(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  /* Square */
  
  def getSquares: Task[List[SquareRow]] =
    SquareRepositoryDefault.get
      .provide(squareRepositoryDefault, ctx, con)

  def createSquare(square: Square): Task[List[Long]] =
    SquareRepositoryDefault.create(square)
      .provide(squareRepositoryDefault, ctx, con)

  def updateSquare(square: Square): Task[SquareRow] =
    SquareRepositoryDefault.update(square)
      .provide(squareRepositoryDefault, ctx, con)

  def deleteSquare(square: Square): Task[SquareRow] =
    SquareRepositoryDefault.delete(square)
      .provide(squareRepositoryDefault, ctx, con)

  /* User */

  def getUsers: Task[List[UserRow]] =
    UserRepositoryDefault.get
      .provide(userRepositoryDefault, ctx, con)

  def createUser(user: User): Task[List[Long]] =
    UserRepositoryDefault.create(user)
      .provide(userRepositoryDefault, ctx, con)

  def updateUser(user: User): Task[UserRow] =
    UserRepositoryDefault.update(user)
      .provide(userRepositoryDefault, ctx, con)

  def deleteUser(user: User): Task[UserRow] =
    UserRepositoryDefault.delete(user)
      .provide(userRepositoryDefault, ctx, con)

}
