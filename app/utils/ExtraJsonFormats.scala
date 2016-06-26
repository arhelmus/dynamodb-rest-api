package utils

import java.time.LocalDate

import com.wix.accord.Violation
import models.Fuel
import play.api.libs.json.{JsSuccess, _}

trait ExtraJsonFormats {

  implicit val dateWrites = new Writes[LocalDate] {
    override def writes(c: LocalDate) = JsNumber(c.toEpochDay)
  }

  implicit val dateReads = new Reads[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] = JsSuccess(LocalDate.ofEpochDay(json.as[Long]))
  }

  implicit val fuelWrites = new Writes[Fuel] {
    override def writes(c: Fuel) = JsString(c.toString)
  }

  implicit val fuelReads = new Reads[Fuel] {
    override def reads(json: JsValue): JsResult[Fuel] = JsSuccess(Fuel.apply(json.as[String]))
  }

  implicit val violationWrites = new Writes[Violation] {
    override def writes(o: Violation): JsValue = JsObject(Seq(
      "field" -> JsString(o.description.getOrElse("")),
      "error" -> JsString(o.constraint)
    ))
  }

}
