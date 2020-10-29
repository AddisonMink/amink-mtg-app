package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import requests.delete.DeleteUserRequest
import requests.post.PostUserRequest
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

  def getUserLatest(id: Long): Action[AnyContent] = Action.async { _ =>
    service.getUserLatest(id).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(Json.toJson(user))
    }
  }

  def postUser: Action[PostUserRequest] = Action.async(parse.form(PostUserRequest.form)) { request =>
    for {
      user <- service.addUser(request.body)
    } yield Created(Json.toJson(user))
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async { _ =>
    for {
      successful <- service.deleteUser(id)
    } yield if(successful) NoContent else NotFound
  }

  def postDeleteUser: Action[DeleteUserRequest] = Action.async(parse.form(DeleteUserRequest.form)) { request =>
    for {
      _ <- service.deleteUser(request.body.id)
    } yield Ok(Json.obj("message" -> s"Deleted user with id ${request.body.id}"))
  }
}
