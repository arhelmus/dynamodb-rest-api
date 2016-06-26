package utils

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.github.dwhjames.awswrap.dynamodb.{AmazonDynamoDBScalaClient, AmazonDynamoDBScalaMapper}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait DynamoDbSpec {
  private val sdkClient = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials("FAKE_ACCESS_KEY", "FAKE_SECRET_KEY"))
  sdkClient.setEndpoint("http://localhost:8000")
  private val client = new AmazonDynamoDBScalaClient(sdkClient)
  val db = AmazonDynamoDBScalaMapper(client)

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
