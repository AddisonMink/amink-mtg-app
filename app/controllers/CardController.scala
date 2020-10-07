package controllers

import cats.data.{EitherT, OptionT}
import dao.{CardDao, UserDao}
import javax.inject.Inject
import models.ApiCard
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import requests.post.PostBoosterRequest
import responses.GetBoosterResponse
import services.{BoosterService, CardService}

import scala.concurrent.{ExecutionContext, Future}

class CardController @Inject()(
  val controllerComponents: ControllerComponents,
  service: CardService,
  userDao: UserDao,
  cardDao: CardDao
)(implicit ec: ExecutionContext) extends BaseController {

  def getBooster(setId: String): Action[AnyContent] = Action.async { _ =>
    val futureOpt = for {
      set <- OptionT(service.fetchSet(setId))
      cards = BoosterService.assembleBooster(set)
    } yield GetBoosterResponse(cards)

    futureOpt.value.map {
      case None => NotFound(Json.obj("message" -> s"Could not fetch set with id $setId."))
      case Some(value) => Ok(Json.toJson(value))
    }
  }

  def addBooster: Action[PostBoosterRequest] = Action.async(parse.form(PostBoosterRequest.form)) { implicit request =>
    val futureEither: EitherT[Future, String, GetBoosterResponse] = for {
      _ <- EitherT.fromOptionF(userDao.getUser(request.body.userId), s"No user with id ${request.body.userId} exists.")
      set <- EitherT.fromOptionF(service.fetchSet(request.body.setId), s"Could not fetch set with id ${request.body.setId}.")
      cards = (1 to request.body.number).flatMap(_ => BoosterService.assembleBooster(set))
      _ <- EitherT.liftF(cardDao.insertCards(request.body.userId, cards))
    } yield GetBoosterResponse(cards)

    futureEither.value.map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
