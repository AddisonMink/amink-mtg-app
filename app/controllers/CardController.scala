package controllers

import cats.data.{EitherT, OptionT}
import dao.{CardDao, UserDao}
import javax.inject.Inject
import models.ApiCard
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
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

  def addBooster(setId: String, userId: Long): Action[AnyContent] = Action.async { _ =>
    val futureEither: EitherT[Future, String, GetBoosterResponse] = for {
      _ <- EitherT.fromOptionF(userDao.getUser(userId), s"No user with id $userId exists.")
      set <- EitherT.fromOptionF(service.fetchSet(setId), s"Could not fetch set with id $setId.")
      cards = BoosterService.assembleBooster(set)
      _ <- EitherT.liftF(cardDao.insertCards(userId, cards))
    } yield GetBoosterResponse(cards)

    futureEither.value.map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(value) => Ok(Json.toJson(value))
    }
  }
}
