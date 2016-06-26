import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerTest, OneServerPerTest, PlaySpec}

/**
  * Testing is our application can launch up to be sure that DI configured fine.
  */
class ServiceIntegrationSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with HtmlUnitFactory {

  "Service" should {

    "launch up" in {
      go to s"http://localhost:$port/api/health/isAlive"
      pageSource must be ("Alive")
    }

  }

}
