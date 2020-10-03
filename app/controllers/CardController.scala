package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import responses.GetBoosterResponse
import services.{BoosterService, CardService}

import scala.concurrent.ExecutionContext

class CardController @Inject()(
  val controllerComponents: ControllerComponents,
  service: CardService
)(implicit ec: ExecutionContext) extends BaseController {

  def getBooster(setId: String): Action[AnyContent] = Action.async { _ =>
    for {
      setOpt <- service.fetchSet(setId)
      result = setOpt match {
        case None => NotFound(Json.obj("message" -> s"Could not fetch set with id $setId."))
        case Some(set) =>
          val cards = BoosterService.assembleBooster(set)
          Ok(Json.toJson(GetBoosterResponse(cards)))
      }
    } yield result
  }
}
