package controllers

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SignUpControllerSpec extends PlayFunSpec with GuiceOneAppPerSuite {

  describe("SignUpController") {
    describe("route of SingUpController#index") {
      it("should be valid") {
        val result = route(app, addCsrfToken(FakeRequest(GET, routes.SignUpController.index().toString))).get
        status(result) mustBe OK
      }
    }
  }
}
