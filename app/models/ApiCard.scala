package models

import play.api.libs.json.Json

case class ApiCard (
  name: String,
  set: String,
  rarity: String,
  colors: Seq[String],
  imageUrl: String,
)

object ApiCard {
  implicit val format = Json.format[ApiCard]
}

