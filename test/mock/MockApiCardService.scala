package mock

import models.{ApiCard, CardSet}
import services.ApiCardService

import scala.concurrent.Future

object MockApiCardService extends ApiCardService {

  val commonCard = ApiCard("commonCard", "", "Common", Seq(), "")
  val uncommonCard = ApiCard("uncommonCard", "", "Uncommon", Seq(), "")
  val rareCard = ApiCard("rareCard", "", "", Seq(), "")

  override def fetchSet(setId: String): Future[Option[CardSet]] = {
    if(setId == "mock") {
      val set = CardSet(
        "mock",
        Seq.fill(15)(commonCard),
        Seq.fill(15)(uncommonCard),
        Seq.fill(15)(rareCard)
      )
      Future.successful(Some(set))
    } else {
      Future.successful(None)
    }
  }
}
