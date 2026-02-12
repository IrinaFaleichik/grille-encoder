package irka.grilleEncoder
package infrastructure.db

import domain.model.{CardboardId, SquareId, UserId}

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import application.api.auth.dto.{AuthUserDto, Role}

// DB entities (what maps to tables)
package object entities:
  object TableEntity:
    case class Cardboard(id: CardboardId, name: String, userId: String)

    case class Square(
                       id: SquareId,
                       cardboardId: CardboardId, // Foreign key from cardboard.id, required
                       startX: Int,
                       startY: Int,
                       endX: Int,
                       endY: Int
                     )

    case class User(id: UserId, name: String)

    case class AuthUserEntity(
                         id: UserId,
                         username: String,
                         passwordHash: String,
                         role: Int = Role.User.ordinal,
                         email: Option[String] = None
                       ):
      def toDto: AuthUserDto = AuthUserDto(id, username, email, Role.fromOrdinal(role))


  type DBContext = Quill.Sqlite[SnakeCase]

  object DBContext:
    val namingStrategy: SnakeCase.type = SnakeCase
