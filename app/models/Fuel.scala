package models

sealed trait Fuel

case object Diesel extends Fuel {
  override def toString() = "diesel"
}

case object Gasoline extends Fuel {
  override def toString() = "gasoline"
}

object Fuel extends FuelSerializer {
  def apply(fuelName: String) = fuelName match {
    case "diesel" => Diesel
    case "gasoline" => Gasoline
  }
}

trait FuelSerializer {

  import com.amazonaws.services.dynamodbv2.model.AttributeValue

  implicit val attributeValueToFuel: AttributeValue => Fuel = (x: AttributeValue) => x.getN.toByte match {
    case 1 => Diesel
    case 2 => Gasoline
  }

  implicit val fuelToAttributeValue = (fuel: Fuel) => {
    val serializedVal = fuel match {
      case Diesel => 1
      case Gasoline => 2
    }

    new AttributeValue().withN(serializedVal.toString)
  }

}
