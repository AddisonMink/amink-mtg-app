package service

import org.scalatest.time.Minutes
import org.scalatestplus.play.PlaySpec
import play.api.test.WsTestClient
import services.{CardService, CardServiceImpl}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MINUTES

class CardServiceSpec extends PlaySpec {

  "CardService.getSpec" should {

    "return all non-basic land cards in a set, partitioned by rarity" in {
      WsTestClient.withClient { client =>
        val service = new CardServiceImpl(client)
        val result = Await.result(service.fetchSet("ODY"), Duration(5,MINUTES)).get
        result.id mustBe "ODY"
        result.commons.length mustBe 110
        result.commons.forall(_.rarity == "Common") mustBe true
        result.uncommons.length mustBe 110
        result.uncommons.forall(_.rarity == "Uncommon") mustBe true
        result.rares.length mustBe 110
        result.rares.forall(c => c.rarity != "Common" && c.rarity != "Uncommon")
      }
    }
  }
}
