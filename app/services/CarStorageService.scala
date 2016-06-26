package services

import models.Car
import models.Car.CarId
import utils.{ASC, DESC, SortingDirection}

import scala.concurrent.Future

trait CarStorageService {

  def getAllCars[F](sortBy: Car => F, direction: SortingDirection): Future[Seq[Car]]

  def getCar(id: CarId): Future[Option[Car]]

  def addCar(car: Car): Future[Car]

  def updateCar(id: CarId, car: Car): Future[Car]

  def deleteCar(id: CarId): Future[Unit]

}

class InMemoryCarStorageService extends CarStorageService {

  override def getAllCars[F](sortBy: Car => F, direction: SortingDirection): Future[Seq[Car]] =
    Future.successful {
      val unsortedCars = innerState.values.toSeq
      direction match {
        case ASC => unsortedCars.sortWith((c1, c2) => sortBy(c1).toString > sortBy(c2).toString)
        case DESC => unsortedCars.sortWith((c1, c2) => sortBy(c1).toString < sortBy(c2).toString)
      }
    }

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