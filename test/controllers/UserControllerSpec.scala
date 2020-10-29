package controllers

import dao.{CardDao, UserDao}
import mock.{MockApiCardService, MockCardDao, MockUserDao}
import models.User
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
import requests.post.PostUserRequest
import responses.GetUsersResponse
import services.ApiCardService

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS


class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  val userDao = new MockUserDao()
  val cardDao = new MockCardDao(userDao)

  val user1 = userDao.user1
  val user2 = userDao.user2

  implicit override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .overrides(
        bind[UserDao].toInstance(userDao),
        bind[CardDao].toInstance(cardDao),
        bind[ApiCardService].toInstance(MockApiCardService)
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

  "GET /api/user/{id}" should {

    "return the user data if it exists" in {
      val result = route(app, FakeRequest("GET", "/api/user/1")).get
      val content = contentAsJson(result).as[User]
      status(result) mustBe OK
      content.id mustBe 1
      content.name mustBe user1.name
      content.cards must contain theSameElementsAs cardDao.mockCards(1)
    }

    "return 404 otherwise" in {
      val result = route(app, FakeRequest("GET", "/api/user/5")).get
      status(result) mustBe NOT_FOUND
    }
  }

  "GET /api/user/{id}/latest" should {

    "return the user data if it exists, including only the most recent batch of cards." in {
      val result = route(app, FakeRequest("GET", "/api/user/1/latest")).get
      val content = contentAsJson(result).as[User]
      status(result) mustBe OK
      content.id mustBe 1
      content.name mustBe user1.name
      content.cards must contain theSameElementsAs cardDao.mockCards(1).filter(_.batch == user1.batch)
    }

    "return 404 otherwise" in {
      val result = route(app, FakeRequest("GET", "/api/user/5/latest")).get
      status(result) mustBe NOT_FOUND
    }
  }

  "POST /api/user/" should {

    "add a user" in {
      val body = Json.toJson(PostUserRequest("test"))
      val result = route(app, FakeRequest("POST", "/api/user/").withBody(body)).get

      status(result) mustBe CREATED

      val content = contentAsJson(result).as[User]
      content.name mustBe "test"

      val user = Await.result(userDao.getUser(content.id), Duration(30,SECONDS))
      user mustBe Some(User(content.id,content.name, batch = content.batch))
    }

    "report an invalid request body" in {
      val body = Json.toJson(GetUsersResponse(Seq(user1)))
      val result = route(app, FakeRequest("POST", "/api/user/").withBody(body)).get

      status(result) mustBe BAD_REQUEST
    }
  }

  "DELETE /api/user/{id}" should {

    "delete a user if it exists" in {
      val result = route(app, FakeRequest("DELETE", "/api/user/1")).get

      status(result) mustBe NO_CONTENT

      val user = Await.result(userDao.getUser(1), Duration(30,SECONDS))
      user mustBe None

      val cards = Await.result(cardDao.getUserCards(1), Duration(30,SECONDS))
      cards mustBe empty
    }

    "return 404 otherwise" in {
      val result = route(app, FakeRequest("DELETE", "/api/user/9999")).get

      status(result) mustBe NOT_FOUND
    }
  }
}
