package services

import com.amazonaws.services.dynamodbv2.model.{GetItemRequest, ScanRequest}
import com.github.dwhjames.awswrap.dynamodb._
import models.Car
import models.Car.CarId
import models.tables.CarTable
import utils.{ASC, DESC, SortingDirection}

import scala.concurrent.{ExecutionContext, Future}

trait CarStorageService {

  def getAllCars[F](sortBy: Car => F, direction: SortingDirection): Future[Seq[Car]]

  def getCar(id: CarId): Future[Option[Car]]

  def addCar(car: Car): Future[Car]

  def updateCar(id: CarId, car: Car): Future[Car]

  def deleteCar(id: CarId): Future[Unit]

}

class DynamoDbCarStorageService(db: AmazonDynamoDBScalaMapper)(implicit executionContext: ExecutionContext) extends CarStorageService {

  private implicit val dbSerializer = CarTable.dynamoDbSerializer

  override def getAllCars[F](sortBy: (Car) => F, direction: SortingDirection): Future[Seq[Car]] =
     db.scan().map(SortingDirection.sortBy(_, sortBy, direction))

  override def addCar(car: Car): Future[Car] =
    db.dump[Car](car).map(_ => car)

  override def updateCar(id: CarId, car: Car): Future[Car] = {
    val updatedCar = car.copy(id = id)
    db.dump[Car](updatedCar).map(_ => updatedCar)
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
    Future.successful {
      innerState = innerState + (car.id -> car)
      car
    }

  override def updateCar(id: CarId, car: Car): Future[Car] =
    Future.successful {
      innerState = innerState.updated(id, car)
      car
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
