package irka.grilleEncoder
package domain

package object model {

  import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

  final type UserId = String
  final type CardboardId = String
  final type SquareId = String

  final case class User(id: UserId, name: String, cardboards: Option[List[Cardboard]] = None)

  object User {
    implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
    implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  }

  final class Text // todo to put under the cardboard?????

  final case class Cardboard(id: CardboardId, name: String, squares: List[Square], userId: UserId)

  object Cardboard {
    implicit val encoder: JsonEncoder[Cardboard] = DeriveJsonEncoder.gen[Cardboard]
    implicit val decoder: JsonDecoder[Cardboard] = DeriveJsonDecoder.gen[Cardboard]
  }

  final case class Square(id: SquareId, start: Point, end: Point, cardboard: Cardboard)

  object Square {
    implicit val encoder: JsonEncoder[Square] = DeriveJsonEncoder.gen[Square]
    implicit val decoder: JsonDecoder[Square] = DeriveJsonDecoder.gen[Square]
  }

  final case class Point(x: Int, y: Int) {
    def asInts: (Int, Int) = (x, y)
  } // get from the front

  object Point {
    implicit val encoder: JsonEncoder[Point] = DeriveJsonEncoder.gen[Point]
    implicit val decoder: JsonDecoder[Point] = DeriveJsonDecoder.gen[Point]
  }
}
