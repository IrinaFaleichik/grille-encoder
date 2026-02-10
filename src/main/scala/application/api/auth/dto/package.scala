package irka.grilleEncoder
package application.api.auth

import irka.grilleEncoder.domain.model.UserId
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

package object dto:
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

  object AuthUserDto:
    implicit val encoder: JsonEncoder[AuthUserDto] = DeriveJsonEncoder.gen[AuthUserDto]
    implicit val decoder: JsonDecoder[AuthUserDto] = DeriveJsonDecoder.gen[AuthUserDto]