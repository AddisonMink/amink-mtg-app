package responses

import models.User
import play.api.libs.json.Json

case class GetUsersResponse(
  users: Seq[User]
)

object GetUsersResponse {
  implicit val format = Json.format[GetUsersResponse]
}
