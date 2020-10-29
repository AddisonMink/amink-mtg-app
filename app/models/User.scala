package models

import play.api.libs.json.Json

case class User(
  id: Long,
  name: String,
  cards: Seq[DbCard] = Seq(),
  batch : Int
)

object User {
  implicit val format = Json.format[User]
}