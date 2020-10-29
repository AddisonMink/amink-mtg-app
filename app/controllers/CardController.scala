package controllers
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, BaseController, ControllerComponents}
import requests.post.PostBoosterRequest
import responses.GetBoosterResponse
import services.DbCardService

import scala.concurrent.ExecutionContext

class CardController @Inject()(
  val controllerComponents: ControllerComponents,
  service: DbCardService
)(implicit ec: ExecutionContext) extends BaseController {

  def addBooster: Action[PostBoosterRequest] = Action.async(parse.form(PostBoosterRequest.form)) { implicit request =>
    val userId = request.body.userId
    val setId = request.body.setId
    val num = request.body.number

    service.giveUserBoosters(userId,setId,num).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(value) => Ok(Json.toJson(GetBoosterResponse(value)))
    }
  }
}
