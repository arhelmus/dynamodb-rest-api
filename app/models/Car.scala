package models

import java.time.LocalDate
import com.wix.accord.dsl._

import models.Car.CarId

case class Car(
  id: CarId,
  title: String,
  fuel: Fuel,
  price: Int,
  `new`: Boolean, // named field like this just because its one of requirements ;)
  mileage: Option[Int],
  firstRegistration: Option[LocalDate]
)

object Car {

  type CarId = String

  def newCar(id: CarId, title: String, fuel: Fuel, price: Int) =
    new Car(id, title, fuel, price, true, None, None)

  def oldCar(id: CarId, title: String, fuel: Fuel, price: Int, mileage: Int, firstRegistration: LocalDate) =
    new Car(id, title, fuel, price, false, Some(mileage), Some(firstRegistration))

  def testCar(id: CarId) =
    Car.newCar(id, "Test", Gasoline, 0)

  implicit val carValidator = validator[Car] { c =>
    c.id is notEmpty and notNull
    c.title is notEmpty and notNull
    c.fuel is notNull
    c.price is >= (0)
    !c.`new` || (c.`new` && c.mileage.isEmpty && c.firstRegistration.isEmpty) is true
  }

}