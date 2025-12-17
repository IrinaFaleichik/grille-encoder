package irka.grilleEncoder.infrastructure.db

import irka.grilleEncoder.infrastructure.db.entities.{DBContext, RowObject}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.jdbczio.Quill
import io.getquill.{SnakeCase, SqliteDialect}
import irka.grilleEncoder.domain.model.{Cardboard, Square, User}
import zio.{ExitCode, IO, Task, ULayer, URIO, ZIO, ZIOAppDefault, ZLayer}

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
  def getCardboards: Task[List[RowObject.Cardboard]] =
    CardboardRepositoryDefault.get
      .provide(cardboardRepositoryDefault, ctx, con)

  def createCardboard(cardboard: Cardboard): Task[List[Long]] =
    CardboardRepositoryDefault.create(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  def updateCardboard(cardboard: Cardboard): Task[Cardboard] =
    CardboardRepositoryDefault.update(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  def deleteCardboard(cardboard: Cardboard): Task[Cardboard] =
    CardboardRepositoryDefault.delete(cardboard)
      .provide(cardboardRepositoryDefault, ctx, con)

  /* Square */
  
  def getSquares: Task[List[Square]] =
    SquareRepositoryDefault.get
      .provide(squareRepositoryDefault, ctx, con)

  def createSquare(square: Square): Task[List[Long]] =
    SquareRepositoryDefault.create(square)
      .provide(squareRepositoryDefault, ctx, con)

  def updateSquare(square: Square): Task[Square] =
    SquareRepositoryDefault.update(square)
      .provide(squareRepositoryDefault, ctx, con)

  def deleteSquare(square: Square): Task[Square] =
    SquareRepositoryDefault.delete(square)
      .provide(squareRepositoryDefault, ctx, con)

  /* User */

  def getUsers: Task[List[User]] =
    UserRepositoryDefault.get
      .provide(userRepositoryDefault, ctx, con)

  def createUser(user: User): Task[List[Long]] =
    UserRepositoryDefault.create(user)
      .provide(userRepositoryDefault, ctx, con)

  def updateUser(user: User): Task[User] =
    UserRepositoryDefault.update(user)
      .provide(userRepositoryDefault, ctx, con)

  def deleteUser(user: User): Task[User] =
    UserRepositoryDefault.delete(user)
      .provide(userRepositoryDefault, ctx, con)

}

object test extends ZIOAppDefault {
      override def run: URIO[Any, ExitCode] = {
        Api.getUsers.debug("Results").exitCode
      }
}