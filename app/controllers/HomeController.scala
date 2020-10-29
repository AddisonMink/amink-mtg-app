package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import requests.post.{PostBoosterRequest, PostUserRequest}
import services.UserService

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  service: UserService,
  messagesAction: MessagesActionBuilder
)(implicit ec: ExecutionContext) extends BaseController {

  def indexView: Action[AnyContent] = Action.async { _ =>
    service.getUsers.map { users =>
      Ok(views.html.index(users))
    }
  }

  def userView(id: Long): Action[AnyContent] = Action.async { _ =>
    service.getUser(id).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(views.html.user(user))
    }
  }

  def userLatestView(id: Long): Action[AnyContent] = Action.async { _ =>
    service.getUserLatest(id).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(views.html.user(user))
    }
  }

  def adminView: Action[AnyContent] = messagesAction { implicit request =>
    Ok(views.html.admin())
  }
}
