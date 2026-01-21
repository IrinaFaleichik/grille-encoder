package irka.grilleEncoder
package application.api.auth

import domain.model.UserId
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class AuthUser(id: UserId, username: String, password: PasswordHash, email: Option[String] = None, role: Role = Role.User)

object AuthUser:
  implicit val encoder: JsonEncoder[AuthUser] = DeriveJsonEncoder.gen[AuthUser]
  implicit val decoder: JsonDecoder[AuthUser] = DeriveJsonDecoder.gen[AuthUser]

enum Role:
  case User, Admin

object Role:
  implicit val encoder: JsonEncoder[Role] = JsonEncoder[String].contramap:
    case Role.Admin => "Admin"
    case Role.User => "User"

  implicit val decoder: JsonDecoder[Role] = JsonDecoder[String].map:
    case "Admin" => Role.Admin
    case "User" => Role.User

// Response DTO with auth info
case class AuthUserDto(id: UserId, username: String, email: Option[String], role: Role)

//object AuthUserDto {
//  implicit val encoder: JsonEncoder[AuthUserDto] = DeriveJsonEncoder.gen[AuthUserDto]
//  implicit val decoder: JsonDecoder[AuthUserDto] = DeriveJsonDecoder.gen[AuthUserDto]
//}

//class AuthUserDto {
//
//  import zio.Config.Secret
//
//  case class AuthUser(id: UserId, username: String, password: Secret, email: Option[String] = None, role: Role = Role.User)
//
//  enum Role:
//    case User, Admin
//}
