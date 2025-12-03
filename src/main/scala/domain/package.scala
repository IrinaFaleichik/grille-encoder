package irka.grilleEncoder.domain

package object model {
  import zio.json.{JsonEncoder, JsonDecoder, DeriveJsonEncoder, DeriveJsonDecoder}

  final type UserId = String
  final case class User(id: UserId, name: String, cardboards: List[Cardboard])

  object User {
    implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
    implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  }
  final class Text // todo to put under the cardboard?????

  final type CardboardId = String
  final case class Cardboard(id: CardboardId, name: String, squares: List[Square], user: User)
  object Cardboard {
    implicit val encoder: JsonEncoder[Cardboard] = DeriveJsonEncoder.gen[Cardboard]
    implicit val decoder: JsonDecoder[Cardboard] = DeriveJsonDecoder.gen[Cardboard]
  }

  final type SquareId = String
  final case class Square(id: SquareId, start: Point, end: Point, cardboard: Cardboard)

  object Square {
      implicit val encoder: JsonEncoder[Square] = DeriveJsonEncoder.gen[Square]
      implicit val decoder: JsonDecoder[Square] = DeriveJsonDecoder.gen[Square]
    }

  final case class Point(x: Int, y: Int) {
    def asInts: (Int, Int) = (x, y)
  } // get from front

  object Point {
    implicit val encoder: JsonEncoder[Point] = DeriveJsonEncoder.gen[Point]
    implicit val decoder: JsonDecoder[Point] = DeriveJsonDecoder.gen[Point]
  }
}
