package controllers

import dao.UserDao
import mock.MockUserDao
import model.User
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
import responses.GetUsersResponse


class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  implicit override def newAppForTest(testData: TestData): Application = {
    GuiceApplicationBuilder()
      .overrides(bind[UserDao].toInstance(MockUserDao))
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
      content mustBe user1
    }
  }
}
