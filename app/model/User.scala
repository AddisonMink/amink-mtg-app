package model

import play.api.libs.json.Json

case class User(
  id: Long,
  name: String
)

object User {
  implicit val format = Json.format[User]
}