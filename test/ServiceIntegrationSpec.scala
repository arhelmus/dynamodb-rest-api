import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerTest, OneServerPerTest, PlaySpec}
import play.api.test.Helpers._

class ServiceIntegrationSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with HtmlUnitFactory with ScalaFutures {

  "Service" should {

    "launch up" in {
      go to s"http://localhost:$port/api/health/isAlive"
      pageSource must be("Alive")
    }

    "answer on preflight requests" in {
      val origin = "http://archdev.me"
      val requestMethod = "PUT"
      val request = wsUrl("/api/health/isAlive").withHeaders(
        "Host" -> s"localhost:$port",
        "Origin" -> origin,
        "Access-Control-Request-Method" -> requestMethod
      ).options()

      whenReady(request) { response =>
        response.status mustBe OK
        response.header("Access-Control-Allow-Credentials") mustBe Some("true")
        response.header("Access-Control-Allow-Origin") mustBe Some(origin)
        response.header("Access-Control-Allow-Methods") mustBe Some(requestMethod)
      }
    }

  }

}
