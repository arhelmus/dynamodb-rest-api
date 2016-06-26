import org.scalatestplus.play._
import play.api.libs.json._
import play.api.mvc.AnyContentAsJson
import play.api.test.Helpers._
import play.api.test._

class CarRestControllerSpec extends PlaySpec with OneAppPerSuite {

  "Car rest controller" should {

    "retrieve all cars by GET request" in new Context {
      val response = doRequest(FakeRequest(GET, basePath).withJsonBody(JsObject(Nil)))

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe JsArray(Seq(carJsObject("test"), carJsObject("test2")))
    }

    "retrieve sorted cars by GET request" in new Context {
      val response = doRequest(FakeRequest(GET, basePath + "?sortField=id&sortDirection=DESC").withJsonBody(JsObject(Nil)))

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe JsArray(Seq(carJsObject("test2"), carJsObject("test")))
    }

    "retrieve car with specified id by GET request" in new Context {
      val response = doRequest(FakeRequest(GET, basePath + "/id").withJsonBody(JsObject(Nil)))

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe carJsObject("id")
    }

    "show 404 if car not found by id on GET request" in new Context {
      val response = doRequest(FakeRequest(GET, basePath + "/notFound").withJsonBody(JsObject(Nil)))

      status(response) mustBe NOT_FOUND
    }

    "add car with specified id by POST request" in new Context {
      val newCar = carJsObject("notFound")
      val response = doRequest(FakeRequest(POST, basePath).withJsonBody(newCar))

      status(response) mustBe CREATED
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe newCar
    }

    "show 409 if car with specified id already exists on POST request" in new Context {
      val newCar = carJsObject("id")
      val response = doRequest(FakeRequest(POST, basePath).withJsonBody(newCar))

      status(response) mustBe CONFLICT
    }

    "show 400 if json is incorrect on POST request" in new Context {
      val newCar = carJsObject("id")
      val response = doRequest(FakeRequest(POST, basePath).withJsonBody(JsObject(Nil)))

      status(response) mustBe BAD_REQUEST
    }

    "update car with specified id by PUT request" in new Context {
      val updatedCar = carJsObject("id", "updated car")
      val response = doRequest(FakeRequest(PUT, basePath + "/id").withJsonBody(updatedCar))

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsJson(response) mustBe updatedCar
    }

    "show 404 if car with specified id not found on PUT request" in new Context {
      val updatedCar = carJsObject("notFound")
      val response = doRequest(FakeRequest(PUT, basePath + "/notFound").withJsonBody(updatedCar))

      status(response) mustBe NOT_FOUND
    }

    "show 400 if json is incorrect on PUT request" in new Context {
      val newCar = carJsObject("id")
      val response = doRequest(FakeRequest(PUT, basePath + "/id").withJsonBody(JsObject(Nil)))

      status(response) mustBe BAD_REQUEST
    }

    "delete car with specified id by DELETE request" in new Context {
      val response = doRequest(FakeRequest(DELETE, basePath + "/id").withJsonBody(JsObject(Nil)))

      status(response) mustBe OK
      contentType(response) mustBe None
    }

    "show 404 if car with specified id not found on DELETE request" in new Context {
      val response = doRequest(FakeRequest(DELETE, basePath + "/notFound").withJsonBody(JsObject(Nil)))
      status(response) mustBe NOT_FOUND
    }

  }

  trait Context {
    val basePath = "/api/cars"
    def doRequest(request: FakeRequest[AnyContentAsJson]) = route(app, request).get

    def carJsObject(id: String, title: String = "Test") = JsObject(Seq(
      "id" -> JsString(id),
      "title" -> JsString(title),
      "fuel" -> JsString("gasoline"),
      "price" -> JsNumber(0),
      "new" -> JsBoolean(true),
      "mileage" -> JsNull,
      "firstRegistration" -> JsNull
    ))
  }

}
