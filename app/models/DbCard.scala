package models

import play.api.libs.json.Json

case class DbCard(
  playerId: Long,
  name: String,
  colors: Seq[String],
  imageUrl: String,
  batch: Int
)

object DbCard {
  implicit val format = Json.format[DbCard]
}