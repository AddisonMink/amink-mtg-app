package controllers

import dao.{CardDao, UserDao}
import mock.{MockCardDao, MockCardService, MockUserDao}
import models.User
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting
import play.api.test._
import play.api.test.Helpers._
import mock.MockUserDao._
import org.scalatest.TestData
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import play.api.libs.json.Json
import requests.PostUserRequest
import responses.GetUsersResponse
import services.CardService


class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  implicit override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .overrides(
        bind[UserDao].toInstance(MockUserDao),
        bind[CardDao].toInstance(MockCardDao),
        bind[CardService].toInstance(MockCardService)
      )
      .build()
  }

  "GET /api/user/" should {

    "return the identifiers of all users" in {
      val result = route(app, FakeRequest("GET", "/api/user/")).get
      val content = contentAsJson(result).as[GetUsersResponse]
      status(result) mustBe OK
      content.users must contain theSameElementsAs Seq(user1,user2)
    }
  }

  "GET /api/user/id/{id}" should {

    "return the user data if it exists" in {
      val result = route(app, FakeRequest("GET", "/api/user/id/1")).get
      val content = contentAsJson(result).as[User]
      status(result) mustBe OK
      content.id mustBe 1
      content.name mustBe user1.name
      content.cards must contain theSameElementsAs MockCardDao.mockCards(1)
    }

    "return 404 otherwise" in {
      val result = route(app, FakeRequest("GET", "/api/user/id/5")).get
      status(result) mustBe NOT_FOUND
    }
  }

  "POST /api/user/" should {

    "add a user" in {
      val body = Json.toJson(PostUserRequest("test"))
      val result = route(app, FakeRequest("POST", "/api/user/").withBody(body)).get

      status(result) mustBe OK

      val content = contentAsJson(result).as[User]
      content.name mustBe "test"
    }

    "report an invalid request body" in {
      val body = Json.toJson(GetUsersResponse(Seq(user1)))
      val result = route(app, FakeRequest("POST", "/api/user/").withBody(body)).get

      status(result) mustBe BAD_REQUEST
    }
  }
}
