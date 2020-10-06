package routers

import controllers.UserController
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class UserRouter @Inject()(controller: UserController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => controller.getUsers
    case GET(p"/id/$id") if id.forall(_.isDigit) => controller.getUser(id.toLong)
    case POST(p"/") => controller.addUser
  }
}
