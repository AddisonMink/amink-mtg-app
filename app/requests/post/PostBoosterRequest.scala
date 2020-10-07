package requests.post

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

case class PostBoosterRequest(
  userId: Int,
  setId: String,
  number: Int
)

object PostBoosterRequest {
  implicit val format = Json.format[PostBoosterRequest]
  val form = Form(
    mapping(
      "userId" -> number,
      "setId" -> nonEmptyText,
      "number" -> number
    )(PostBoosterRequest.apply)(PostBoosterRequest.unapply)
  )
}