package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import requests.PostUserRequest
import responses.GetUsersResponse
import services.UserService

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  service: UserService
)(implicit ec: ExecutionContext) extends BaseController {

  def getUsers: Action[AnyContent] = Action.async { _ =>
    service.getUsers.map(users => Ok(Json.toJson(GetUsersResponse(users))))
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { _ =>
    service.getUser(id).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(Json.toJson(user))
    }
  }

  def addUser: Action[PostUserRequest] = Action.async(parse.json[PostUserRequest]) { request =>
    for {
      user <- service.addUser(request.body)
    } yield Ok(Json.toJson(user))
  }
}
