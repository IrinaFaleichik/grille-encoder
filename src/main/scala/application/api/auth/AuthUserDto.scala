package irka.grilleEncoder
package application.api.auth

import domain.model.UserId
import zio.Config.Secret
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import SecretCodecs._

case class AuthUser(id: UserId, username: String, password: Secret, email: Option[String] = None, role: Role = Role.User)

object AuthUser:
  implicit val encoder: JsonEncoder[AuthUser] = DeriveJsonEncoder.gen[AuthUser]
  implicit val decoder: JsonDecoder[AuthUser] = DeriveJsonDecoder.gen[AuthUser]

object SecretCodecs:
  // todo make hash function for password
  // Encoder for Secret - converts Secret to a String
  implicit val secretEncoder: JsonEncoder[Secret] = JsonEncoder.string.contramap(_.stringValue)

  // Decoder for Secret - creates Secret from a String
  implicit val secretDecoder: JsonDecoder[Secret] = JsonDecoder.string.map(Secret(_))

enum Role:
  case User, Admin

object Role:
  implicit val encoder: JsonEncoder[Role] = JsonEncoder[String].contramap:
    case Role.Admin => "Admin"
    case Role.User => "User"

  implicit val decoder: JsonDecoder[Role] = JsonDecoder[String].map:
    case "Admin" => Role.Admin
    case "User" => Role.User

case class AuthRequest(username: String, password: String)

object AuthRequest:
  implicit val encoder: JsonEncoder[AuthRequest] = DeriveJsonEncoder.gen[AuthRequest]
  implicit val decoder: JsonDecoder[AuthRequest] = DeriveJsonDecoder.gen[AuthRequest]


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
