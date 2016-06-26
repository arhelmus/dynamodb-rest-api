package models

import java.time.LocalDate

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

  def apply(id: CarId, title: String, fuel: Fuel, price: Int) =
    new Car(id, title, fuel, price, true, None, None)

  def apply(id: CarId, title: String, fuel: Fuel, price: Int, mileage: Int, firstRegistration: LocalDate) =
    new Car(id, title, fuel, price, false, Some(mileage), Some(firstRegistration))

  def empty(id: CarId) = Car(id, "Test", Gasoline, 0)

  import com.wix.accord.dsl._

  implicit val carValidator = validator[Car] { c =>
    c.id is notEmpty and notNull
    c.title is notEmpty and notNull
    c.fuel is notNull
    c.price is >= (0)
    !c.`new` || (c.`new` && c.mileage.isEmpty && c.firstRegistration.isEmpty) is true
  }

}