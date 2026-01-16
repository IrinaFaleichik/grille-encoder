package irka.grilleEncoder
package application.api.auth

import domain.model.UserId
import zio.Config.Secret
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class AuthUser(id: UserId, username: String, password: Secret, email: Option[String] = None, role: Role = Role.User)

enum Role:
  case User, Admin

case class AuthRequest(username: String, password: String)

object AuthRequest {
  implicit val encoder: JsonEncoder[AuthRequest] = DeriveJsonEncoder.gen[AuthRequest]
  implicit val decoder: JsonDecoder[AuthRequest] = DeriveJsonDecoder.gen[AuthRequest]
}

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
