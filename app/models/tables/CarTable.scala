package models.tables

import com.github.dwhjames.awswrap.dynamodb._
import utils.DynamoDbExtraTypes._
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import models.Car

import scala.collection.mutable

object CarTable {

  protected val tableName = "cars"

  protected object Attributes {
    val id = "id"
    val title = "title"
    val fuel = "fuel"
    val price = "price"
    val `new` = "new"
    val mileage = "mileage"
    val firstRegistration = "firstRegistration"
  }

  val createTableRequest =
    new CreateTableRequest()
      .withTableName(this.tableName)
      .withProvisionedThroughput(
        Schema.provisionedThroughput(10L, 5L))
      .withAttributeDefinitions(
        Schema.stringAttribute(Attributes.id))
      .withKeySchema(
        Schema.hashKey(Attributes.id))

  object dynamoDbSerializer extends DynamoDBSerializer[Car] {

    override def tableName: String = CarTable.this.tableName

    override def hashAttributeName: String = Attributes.id

    override def primaryKeyOf(car: Car) =
      Map(Attributes.id -> car.id)

    override def fromAttributeMap(item: mutable.Map[String, AttributeValue]): Car =
      new Car(
        id = item(Attributes.id),
        title = item(Attributes.title),
        fuel = item(Attributes.fuel),
        price = item(Attributes.price),
        `new` = item(Attributes.`new`),
        // don't know why but scala cannot find implicit converter for class container, so we must suggest
        mileage = attributeValueToOption(item(Attributes.mileage)),
        firstRegistration = attributeValueToOption(item(Attributes.firstRegistration))
      )

    override def toAttributeMap(obj: Car): Map[String, AttributeValue] =
      Map(
        Attributes.id -> obj.id,
        Attributes.title -> obj.title,
        Attributes.fuel -> obj.fuel,
        Attributes.price -> obj.price,
        Attributes.`new` -> obj.`new`,
        Attributes.mileage -> obj.mileage,
        Attributes.firstRegistration -> obj.firstRegistration
      )
  }

}
