package utils

import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration._

trait DynamoDbSpec {

  val connection = new InMemoryDynamoDbConnection

  import connection._

  def provisionTable(createTableRequest: CreateTableRequest) =
    db.client.listTables().flatMap {
      case tables if tables.getTableNames.contains(createTableRequest.getTableName) =>
        db.client.deleteTable(createTableRequest.getTableName)
          .flatMap(_ => db.client.createTable(createTableRequest))
      case _ =>
        db.client.createTable(createTableRequest)
    }

  def awaitProvisionTable(createTableRequest: CreateTableRequest) =
    Await.result(provisionTable(createTableRequest), 5 seconds)

}
