package controllers

import dao.{CardDao, UserDao}
import mock.{MockApiCardService, MockCardDao, MockUserDao}
import models.DbCard
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.TestData
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import play.api.libs.json.Json
import requests.post.PostBoosterRequest
import responses.GetBoosterResponse
import services.ApiCardService

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MINUTES

class CardControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  val userDao = new MockUserDao()
  val cardDao = new MockCardDao(userDao)

  implicit override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .overrides(
        bind[UserDao].toInstance(userDao),
        bind[CardDao].toInstance(cardDao),
        bind[ApiCardService].toInstance(MockApiCardService)
      )
      .build()
  }

  "POST /api/card/booster/" should {

    "add a valid booster pack of cards from the given set to the given user" in {
      val body = Json.toJson(PostBoosterRequest(2,"mock",1))
      val result = route(app, FakeRequest("POST", "/api/card/booster/").withBody(body)).get
      status(result) mustBe OK
      val content = contentAsJson(result).as[GetBoosterResponse]
      content.cards.count(_.rarity == "Common") mustBe 10
      content.cards.count(_.rarity == "Uncommon") mustBe 3
      content.cards.length mustBe 14

      val user = Await.result(userDao.getUser(2), Duration(5,MINUTES)).get
      user.batch mustBe 2
      val cards = Await.result(cardDao.getPlayerCards(2), Duration(5,MINUTES)).get
      val dbCards = content.cards.map(c => DbCard(2,c.name,c.colors,c.imageUrl,2))
      cards must contain theSameElementsAs dbCards
    }

    "return 404 if an invalid setId is given" in {
      val body = Json.toJson(PostBoosterRequest(2,"invalid",1))
      val result = route(app, FakeRequest("POST", "/api/card/booster/").withBody(body)).get
      status(result) mustBe NOT_FOUND
    }

    "return 404 if an invalid userId is given" in {
      val body = Json.toJson(PostBoosterRequest(0,"mock",1))
      val result = route(app, FakeRequest("POST", "/api/card/booster/").withBody(body)).get
      status(result) mustBe NOT_FOUND
    }
  }
}
