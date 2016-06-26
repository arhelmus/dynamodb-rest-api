package utils

import javax.inject.Inject

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.github.dwhjames.awswrap.dynamodb.{AmazonDynamoDBScalaClient, AmazonDynamoDBScalaMapper}
import com.google.inject.ImplementedBy
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._

@ImplementedBy(classOf[AmazonDynamoDbConnection])
trait DynamoDbConnection {
  val db: AmazonDynamoDBScalaMapper
}

class AmazonDynamoDbConnection @Inject() (configuration: Configuration) extends DynamoDbConnection {
  private val credentials = new BasicAWSCredentials(
    configuration.getString("aws.accessKey").get,
    configuration.getString("aws.secretKey").get
  )
  private val sdkClient = new AmazonDynamoDBAsyncClient(credentials)
  private val client = new AmazonDynamoDBScalaClient(sdkClient)

  val db = AmazonDynamoDBScalaMapper(client)
}

class InMemoryDynamoDbConnection extends DynamoDbConnection {
  private val sdkClient = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials("FAKE_ACCESS_KEY", "FAKE_SECRET_KEY"))
  sdkClient.setEndpoint("http://localhost:8000")
  private val client = new AmazonDynamoDBScalaClient(sdkClient)

  val db = AmazonDynamoDBScalaMapper(client)
}