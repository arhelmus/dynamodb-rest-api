package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.wix.accord.Violation
import models.Fuel
import play.api.libs.json.{JsSuccess, _}

trait ExtraJsonFormats {

  implicit val dateWrites = new Writes[LocalDate] {
    override def writes(c: LocalDate) =
      JsString(c.format(DateTimeFormatter.BASIC_ISO_DATE))
  }

  implicit val dateReads = new Reads[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] =
      JsSuccess(LocalDate.from(DateTimeFormatter.BASIC_ISO_DATE.parse(json.as[String])))
  }

  implicit val fuelWrites = new Writes[Fuel] {
    override def writes(c: Fuel) =
      JsString(c.toString)
  }

  implicit val fuelReads = new Reads[Fuel] {
    override def reads(json: JsValue): JsResult[Fuel] =
      JsSuccess(Fuel.apply(json.as[String]))
  }

  implicit val violationWrites = new Writes[Violation] {
    override def writes(o: Violation): JsValue = JsObject(Seq(
      "field" -> JsString(o.description.getOrElse("")),
      "error" -> JsString(o.constraint)
    ))
  }

}
