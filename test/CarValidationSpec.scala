import java.time.LocalDate

import models.{Car, Diesel}
import org.scalatest.{Matchers, WordSpec}
import com.wix.accord._

class CarValidationSpec extends WordSpec with Matchers {

  "Car validation" should {

    "success if car is valid" in new Context {
      shouldBeValid(validCar)
    }

    "fail if id is empty" in new Context {
      shouldBeInvalid(validCar.copy(id = ""))
    }

    "fail if title is empty" in new Context {
      shouldBeInvalid(validCar.copy(title = ""))
    }

    "fail if price is less that zero" in new Context {
      shouldBeInvalid(validCar.copy(price = -1))
    }

    "fail if car is new by have mileage" in new Context {
      shouldBeInvalid(validCar.copy(`new` = true, firstRegistration = None))
    }

    "fail if car is new but have registration date" in new Context {
      shouldBeInvalid(validCar.copy(`new` = true, mileage = None))
    }

  }

  trait Context {
    val validCar = Car.oldCar("carId", "Test car", Diesel, 123, 100000, LocalDate.now())

    def shouldBeValid(car: Car) =
      validate(car).isSuccess should be(true)

    def shouldBeInvalid(car: Car) =
      validate(car).isSuccess should be(false)
  }

}
