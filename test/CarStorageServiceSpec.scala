import java.time.LocalDate
import java.util.UUID

import models.tables.CarTable
import models.{Car, Diesel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Matchers, WordSpec}
import services.{CarStorageService, DynamoDbCarStorageService, InMemoryCarStorageService}
import utils._
import play.api.libs.concurrent.Execution.Implicits._

class InMemoryCarStorageServiceSpec extends CarStorageServiceSpec {
  override val carStorageService: CarStorageService = new InMemoryCarStorageService
}

class DynamoDbCarStorageSpec extends CarStorageServiceSpec with DynamoDbSpec {
  awaitProvisionTable(CarTable.createTableRequest)
  override val carStorageService: CarStorageService = new DynamoDbCarStorageService(connection)
}

abstract class CarStorageServiceSpec extends WordSpec with Matchers with ScalaFutures {

  val carStorageService: CarStorageService

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "Car storage service" should {

    "add and get car by id" in new Context {
      val result = for {
        addedCar <- carStorageService.addCar(testCar)
        retrievedCar <- carStorageService.getCar(carId)
      } yield (addedCar, retrievedCar)

      whenReady(result) { case (addedCar, retrievedCar) =>
        testCar should be(addedCar)
        testCar should be(retrievedCar.get)
      }
    }

    "return None if car not exists when get it by id" in new Context {
      whenReady(carStorageService.getCar("anyUUID"))(_ should be(None))
    }

    "update car by id" in new Context {
      val newTestCar = testCar.copy(title = "Things changes")
      val result = for {
        _ <- carStorageService.addCar(testCar)
        updatedCar <- carStorageService.updateCar(carId, newTestCar)
        retrievedCar <- carStorageService.getCar(carId)
      } yield (updatedCar, retrievedCar)

      whenReady(result) { case (updatedCar, retrievedCar) =>
        newTestCar should be(updatedCar)
        newTestCar should be(retrievedCar.get)
      }
    }

    "delete car by id" in new Context {
      val result = for {
        _ <- carStorageService.addCar(testCar)
        _ <- carStorageService.deleteCar(carId)
        retrievedCar <- carStorageService.getCar(carId)
      } yield retrievedCar

      whenReady(result)(_ should be(None))
    }

    "retrieve sorted list of cars" in new Context {
      val result = for {
        _ <- carStorageService.addCar(testCar)
        _ <- carStorageService.addCar(testCar.copy(id = UUID.randomUUID().toString))

        sortedByIdAsc <- carStorageService.getAllCars(_.id, ASC)
        sortedByIdDesc <- carStorageService.getAllCars(_.id, DESC)
      } yield (sortedByIdAsc, sortedByIdDesc)

      whenReady(result) { case (sortedByIdAsc, sortedByIdDesc) =>
        SortingDirection.isSortedBy[Car, String](sortedByIdAsc, _.id, ASC) should be(true)
        SortingDirection.isSortedBy[Car, String](sortedByIdDesc, _.id, DESC) should be(true)
      }
    }

    "return validation failure on adding of invalid car" in new Context {
      whenReady(carStorageService.addCar(invalidCar).failed)(_ shouldBe a [ValidationException])
    }

    "return validation failure on update with invalid car" in new Context {
      whenReady(carStorageService.updateCar("any", invalidCar).failed)(_ shouldBe a [ValidationException])
    }

    trait Context {
      val carId = UUID.randomUUID().toString
      val testCar = Car.oldCar(carId, "Test car", Diesel, 123, 100000, LocalDate.now())
      val invalidCar = testCar.copy(`new` = true)
    }

  }
}