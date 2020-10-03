package controllers

import dao.{CardDao, UserDao}
import mock.{MockCardDao, MockCardService, MockUserDao}
import models.{DbCard, User}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.TestData
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import responses.GetBoosterResponse
import services.CardService

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MINUTES

class CardControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  implicit override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .overrides(
        bind[UserDao].toInstance(MockUserDao),
        bind[CardDao].toInstance(MockCardDao),
        bind[CardService].toInstance(MockCardService)
      )
      .build()
  }

  "GET /api/card/booster/set/{setId}" should {

    "return a valid booster pack" in {
      val result = route(app, FakeRequest("GET", "/api/card/booster/set/mock")).get
      status(result) mustBe OK
      val content = contentAsJson(result).as[GetBoosterResponse]
      content.cards.count(_.rarity == "Common") mustBe 10
      content.cards.count(_.rarity == "Uncommon") mustBe 3
      content.cards.length mustBe 14
    }

    "return 404 if an invalid setId is given" in {
      val result = route(app, FakeRequest("GET", "/api/card/booster/set/invalid_set_name")).get
      status(result) mustBe NOT_FOUND
    }
  }

  "PUT /api/card/booster/set/{setId}/user/{userId" should {

    "add a valid booster pack of cards from the given set to the given user" in {
      val result = route(app, FakeRequest("PUT", "/api/card/booster/set/mock/user/2")).get
      status(result) mustBe OK
      val content = contentAsJson(result).as[GetBoosterResponse]
      content.cards.count(_.rarity == "Common") mustBe 10
      content.cards.count(_.rarity == "Uncommon") mustBe 3
      content.cards.length mustBe 14

      val cards = Await.result(MockCardDao.getPlayerCards(2), Duration(5,MINUTES)).get
      val dbCards = content.cards.map(c => DbCard(2,c.name,c.colors,c.imageUrl))
      cards must contain theSameElementsAs dbCards
    }

    "return 404 if an invalid setId is given" in {
      val result = route(app, FakeRequest("PUT", "/api/card/booster/set/mock/user/0")).get
      status(result) mustBe NOT_FOUND
    }

    "return 404 if an invalid userId is given" in {
      val result = route(app, FakeRequest("PUT", "/api/card/booster/set/invalid/user/2")).get
      status(result) mustBe NOT_FOUND
    }
  }
}
