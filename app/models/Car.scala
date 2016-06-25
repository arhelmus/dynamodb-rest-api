package models

import java.util.Date

import models.Car.CarId
import models.FuelType.Fuel

case class Car (
  id: CarId,
  title: String,
  fuel: Fuel,
  price: Int,
  `new`: Boolean, // named field like this just because its one of requirements ;)
  mileage: Option[Int],
  firstRegistration: Option[Date]
)

object Car {

  type CarId = String

  def apply(id: CarId, title: String, fuel: Fuel, price: Int) =
    new Car(id, title, fuel, price, false, None, None)

  def apply(id: CarId, title: String, fuel: Fuel, price: Int, mileage: Int, firstRegistration: Date) =
    new Car(id, title, fuel, price, true, Some(mileage), Some(firstRegistration))

  def empty(id: CarId) = Car(id, "", FuelType.gasoline, 0)

}

object FuelType {
  type Fuel = String

  val diesel: Fuel = "diesel"
  val gasoline: Fuel = "gasoline"
}