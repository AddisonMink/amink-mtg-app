package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  service: UserService
)(implicit ec: ExecutionContext) extends BaseController {

  def userView(id: Long): Action[AnyContent] = Action.async { _ =>
    service.getUser(id).map {
      case Left(message) => NotFound(Json.obj("message" -> message))
      case Right(user) => Ok(views.html.user(user))
    }
  }
}
