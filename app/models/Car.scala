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
    new Car(id, title, fuel, price, false, None, None)

  def apply(id: CarId, title: String, fuel: Fuel, price: Int, mileage: Int, firstRegistration: LocalDate) =
    new Car(id, title, fuel, price, true, Some(mileage), Some(firstRegistration))

  def empty(id: CarId) = Car(id, "Test", Gasoline, 0)

}