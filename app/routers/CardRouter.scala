package routers

import controllers.CardController
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class CardRouter @Inject()(controller: CardController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/booster/set/$setId") => controller.getBooster(setId)
    case PUT(p"/booster/set/$setId/user/$userId") if userId.forall(_.isDigit) =>
      controller.addBooster(setId,userId.toLong)
  }
}
