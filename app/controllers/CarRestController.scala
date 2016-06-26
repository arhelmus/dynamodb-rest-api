package controllers

import java.time.LocalDate
import javax.inject.Inject

import models.Car.CarId
import models.{Car, Fuel}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.CarStorageService
import utils.{ASC, ExtraJsonFormats, SortingDirection, ValidationException}

import scala.concurrent.Future

class CarRestController @Inject()(carStorageService: CarStorageService) extends Controller with ExtraJsonFormats {

  def getAllCars(sortField: String, sortDirection: String) = Action.async {
    carStorageService.getAllCars(getFieldByName(sortField), getSortDirection(sortDirection))
      .map(Json.toJson(_)).map(Ok(_))
  }

  def getCarById(id: CarId) = Action.async {
    carStorageService.getCar(id).map {
      case Some(car) => Ok(Json.toJson(car))
      case None => NotFound
    }
  }

  def addCar() = Action.async(parse.json) { implicit r =>
    parseCarFromRequest { carRequest =>
      withCar(carRequest.id)(
        ifExists = _ =>
          Future.successful(Conflict),
        ifNone =
          carStorageService.addCar(carRequest)
            .map(r => Created(Json.toJson(r)))
            .recover(defaultRecoverFunction)
      )
    }
  }

  def updateCar(id: CarId) = Action.async(parse.json) { implicit r =>
    parseCarFromRequest { carRequest =>
      withCar(id)(
        ifNone =
          Future.successful(NotFound),
        ifExists = _ =>
          carStorageService.updateCar(id, carRequest)
            .map(r => Ok(Json.toJson(r)))
            .recover(defaultRecoverFunction)
      )
    }
  }

  def deleteCar(id: CarId) = Action.async {
    withCar(id)(
      ifNone =
        Future.successful(NotFound),
      ifExists = _ =>
        carStorageService.deleteCar(id)
          .map(_ => Ok)
          .recover(defaultRecoverFunction)
    )
  }

  private def withCar(carId: CarId)(ifExists: Car => Future[Result], ifNone: => Future[Result]) =
    carStorageService.getCar(carId).flatMap {
      case Some(c) => ifExists(c)
      case None => ifNone
    }

  private def parseCarFromRequest(action: Car => Future[Result])(implicit request: Request[JsValue]) =
    request.body.asOpt[Car] match {
      case Some(car) => action(car)
      case None => Future.successful(BadRequest)
    }

  private def defaultRecoverFunction: PartialFunction[Throwable, Result] = {
    case ValidationException(violations) =>
      BadRequest(Json.toJson(violations))
    case _ =>
      InternalServerError
  }

  private def getFieldByName(fieldName: String) =
    (car: Car) => fieldName match {
      case "title" => car.title
      case "fuel" => car.fuel
      case "price" => car.price
      case "new" => car.`new`
      case "mileage" => car.mileage
      case "firstRegistration" => car.firstRegistration
      case _ => car.id
    }

  private def getSortDirection(directionName: String) =
    SortingDirection.parse(directionName).getOrElse(ASC)

  private implicit val carsWrites = (
    (JsPath \ "id").write[CarId] and
      (JsPath \ "title").write[String] and
      (JsPath \ "fuel").write[Fuel] and
      (JsPath \ "price").write[Int] and
      (JsPath \ "new").write[Boolean] and
      (JsPath \ "mileage").write[Option[Int]] and
      (JsPath \ "firstRegistration").write[Option[LocalDate]]
    ) (unlift(Car.unapply))

  private implicit val carsReads = (
    (JsPath \ "id").read[CarId] and
      (JsPath \ "title").read[String] and
      (JsPath \ "fuel").read[Fuel] and
      (JsPath \ "price").read[Int] and
      (JsPath \ "new").read[Boolean] and
      (JsPath \ "mileage").readNullable[Int] and
      (JsPath \ "firstRegistration").readNullable[LocalDate]
    ) (Car.apply _)

}
