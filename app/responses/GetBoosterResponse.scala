package responses

import models.ApiCard
import play.api.libs.json.Json

case class GetBoosterResponse(cards: Seq[ApiCard])

object GetBoosterResponse {
  implicit val format = Json.format[GetBoosterResponse]
}
