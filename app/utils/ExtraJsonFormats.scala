package utils

import java.text.SimpleDateFormat
import java.time.{Instant, LocalDate, ZoneId}
import java.util.Date

import com.wix.accord.Violation
import models.Fuel
import play.api.libs.json.{JsSuccess, _}

trait ExtraJsonFormats {

  implicit val dateWrites = new Writes[LocalDate] {
    override def writes(c: LocalDate) =
      JsString(iso8601Formatter.format(c))
  }

  implicit val dateReads = new Reads[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] =
      JsSuccess(toLocalDate(iso8601Formatter.parse(json.as[String])))
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

  private val iso8601Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")
  private def toLocalDate(date: Date) =
    Instant.ofEpochMilli(date.getTime).atZone(ZoneId.systemDefault()).toLocalDate

}
