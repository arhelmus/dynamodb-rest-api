package services

import javax.inject._

import com.github.dwhjames.awswrap.dynamodb._
import com.wix.accord._
import models.Car
import models.Car.CarId
import models.tables.CarTable
import play.api.libs.concurrent.Execution.Implicits._
import utils.{DynamoDbConnection, SortingDirection, ValidationException}

import scala.concurrent.Future

trait CarStorageService {

  def getAllCars[F](sortBy: Car => F, direction: SortingDirection): Future[Seq[Car]]

  def getCar(id: CarId): Future[Option[Car]]

  def addCar(car: Car): Future[Car]

  def updateCar(id: CarId, car: Car): Future[Car]

  def deleteCar(id: CarId): Future[Unit]

}

class DynamoDbCarStorageService @Inject() (dbConnection: DynamoDbConnection) extends CarStorageService {

  import dbConnection._

  private implicit val dbSerializer = CarTable.dynamoDbSerializer

  override def getAllCars[F](sortBy: (Car) => F, direction: SortingDirection): Future[Seq[Car]] =
    db.scan().map(SortingDirection.sortBy(_, sortBy, direction))

  override def addCar(car: Car): Future[Car] =
    validate(car) match {
      case Success =>
        db.dump[Car](car).map(_ => car)
      case Failure(violations) =>
        Future.failed(new ValidationException(violations))
    }


  override def updateCar(id: CarId, car: Car): Future[Car] = {
    val updatedCar = car.copy(id = id)
    validate(updatedCar) match {
      case Success =>
        db.dump[Car](updatedCar).map(_ => updatedCar)
      case Failure(violations) =>
        Future.failed(new ValidationException(violations))
    }
  }

  override def getCar(id: CarId): Future[Option[Car]] =
    db.loadByKey[Car](id)

  override def deleteCar(id: CarId): Future[Unit] =
    db.deleteByKey[Car](id).map(_ => Unit)

}

class InMemoryCarStorageService extends CarStorageService {

  override def getAllCars[F](sortBy: Car => F, direction: SortingDirection): Future[Seq[Car]] =
    Future.successful(SortingDirection.sortBy(innerState.values.toSeq, sortBy, direction))

  override def addCar(car: Car): Future[Car] =
    validate(car) match {
      case Success =>
        Future.successful {
          innerState = innerState + (car.id -> car)
          car
        }
      case Failure(violations) =>
        Future.failed(new ValidationException(violations))
    }

  override def updateCar(id: CarId, car: Car): Future[Car] =
    validate(car) match {
      case Success =>
        Future.successful {
          innerState = innerState.updated(id, car)
          car
        }
      case Failure(violations) =>
        Future.failed(new ValidationException(violations))
    }

  override def getCar(id: CarId): Future[Option[Car]] =
    Future.successful(innerState.get(id))

  override def deleteCar(id: CarId): Future[Unit] =
    Future.successful {
      innerState = innerState - id
      Unit
    }

  private var innerState: Map[CarId, Car] = Map()

}
