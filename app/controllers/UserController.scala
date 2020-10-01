package controllers

import dao.UserDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import responses.GetUsersResponse

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  val dao: UserDao
)(implicit ec: ExecutionContext) extends BaseController {

  def getUsers: Action[AnyContent] = Action.async { _ =>
    for {
      users <- dao.getUsers
      json = Json.toJson(GetUsersResponse(users))
    } yield Ok(json)
  }
}
