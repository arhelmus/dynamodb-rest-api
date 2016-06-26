package models

sealed trait Fuel

case object Diesel extends Fuel {
  override def toString() = "diesel"
}

case object Gasoline extends Fuel {
  override def toString() = "gasoline"
}