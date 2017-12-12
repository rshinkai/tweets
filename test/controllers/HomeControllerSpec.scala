package controllers

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HomeControllerSpec extends PlayFunSpec with GuiceOneAppPerSuite {

  describe("HomeController") {
    describe("route of HomeController#index") {
      it("should be valid") {
        val result = route(app, addCsrfToken(FakeRequest(GET, routes.HomeController.index().toString))).get
        status(result) mustBe OK
      }
    }
  }
}
