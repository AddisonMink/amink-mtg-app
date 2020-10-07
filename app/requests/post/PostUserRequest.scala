package requests.post

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

case class PostUserRequest(
  name: String
)

object PostUserRequest {
  implicit val format = Json.format[PostUserRequest]

  val form = Form(
    mapping(
      "name" -> nonEmptyText
    )(PostUserRequest.apply)(PostUserRequest.unapply)
  )
}