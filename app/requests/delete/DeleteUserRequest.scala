package requests.delete

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

case class DeleteUserRequest(
  id: Int
)

object DeleteUserRequest {
  implicit val format = Json.format[DeleteUserRequest]
  val form = Form(
    mapping(
      "id" -> number
    )(DeleteUserRequest.apply)(DeleteUserRequest.unapply)
  )
}
