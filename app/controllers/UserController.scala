package controllers

import cats.data.EitherT
import dao.{CardDao, UserDao}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import responses.GetUsersResponse

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  userDao: UserDao,
  cardDao: CardDao
)(implicit ec: ExecutionContext) extends BaseController {

  def getUsers: Action[AnyContent] = Action.async { _ =>
    for {
      users <- userDao.getUsers
      json = Json.toJson(GetUsersResponse(users))
    } yield Ok(json)
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { _ =>
    val eitherOption = for {
      user <- EitherT.fromOptionF(userDao.getUser(id), s"No user with id $id exists.")
      cards <- EitherT.fromOptionF(cardDao.getPlayerCards(id), s"Could not retrieve cards for user with id $id.")
    } yield user.copy(cards = cards)

    eitherOption.value.map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(Json.toJson(user))
    }
  }
}
