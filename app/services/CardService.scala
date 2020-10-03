package services

import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.{ApiCard, CardSet}
import play.api.libs.json.JsArray
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CardServiceImpl])
trait CardService {
  def fetchSet(setId: String): Future[Option[CardSet]]
}

class CardServiceImpl @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends CardService {

  private val basicLands = Seq("Swamp", "Plains", "Forest", "Mountain", "Island")

  private var cache: Option[CardSet] = None

  private def fetchCards(setId: String, page: Int = 1): Future[Seq[ApiCard]] = for {
    data <- ws.url(s"https://api.magicthegathering.io/v1/cards?set=$setId&page=$page").get()

    cards = (for {
      data <- Some(data)
      if data.status == 200
      jsonArray <- (data.json \ "cards").asOpt[JsArray]
      cards = jsonArray.value.flatMap(_.asOpt[ApiCard]).toSeq
    } yield cards).getOrElse(Seq())

    linkOpt = data.header("link")
    nextPage = linkOpt.exists(_.contains("rel=\"next\""))
    restCards <- if(nextPage) fetchCards(setId, page+1)
                 else Future.successful(Seq())
  } yield cards ++ restCards

  override def fetchSet(setId: String): Future[Option[CardSet]] = {
    if(cache.exists(_.id == setId)) Future.successful(cache)
    else for {
      allCards <- fetchCards(setId)
      result = if(allCards.isEmpty) None else {
        val cards = allCards.filterNot(c => basicLands.contains(c.name))
        val (commons, rest) = cards.partition(_.rarity == "Common")
        val (uncommons, rares) = rest.partition(_.rarity == "Uncommon")
        val set = Some(CardSet(setId,commons,uncommons,rares))
        cache = set
        set
      }
    } yield result
  }
}