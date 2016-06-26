package utils

import java.time.LocalDate

object DynamoDbExtraTypes {

  import com.github.dwhjames.awswrap.dynamodb._

  implicit val attributeValueToDate = (x: AttributeValue) => LocalDate.ofEpochDay(x.getN.toLong)
  implicit val dateToAttributeValue = (x: LocalDate) => new AttributeValue().withN(x.toEpochDay.toString)

  implicit def attributeValueToOption[A](x: AttributeValue)(implicit innerConverter: AttributeValue => A): Option[A] =
    if(x.isNULL == null) { // .isNULL can be null of true, -_-\
      Some(innerConverter(x))
    } else {
      Option.empty[A]
    }

  implicit def optionToAttributeValue[A](x: Option[A])(implicit innerConverter: A => AttributeValue) =
    x match {
      case Some(value) => innerConverter(value)
      case None => new AttributeValue().withNULL(true)
    }


}
