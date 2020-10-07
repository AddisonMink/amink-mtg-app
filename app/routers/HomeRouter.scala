package routers

import controllers.HomeController
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class HomeRouter @Inject()(controller: HomeController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => controller.indexView
    case GET(p"/admin") => controller.adminView
    case GET(p"/user/$id") if id.forall(_.isDigit) => controller.userView(id.toLong)
  }
}
