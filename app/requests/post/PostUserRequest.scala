package requests.post

import play.api.libs.json.Json

case class PostUserRequest(
  name: String
)

object PostUserRequest {
  implicit val format = Json.format[PostUserRequest]
}