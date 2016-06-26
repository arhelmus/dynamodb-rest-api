import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services.{CarStorageService, DynamoDbCarStorageService, InMemoryCarStorageService}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure() = {
    configuration.getBoolean("db.inMemory").getOrElse(false) match {
      case true =>
        bind(classOf[CarStorageService]).to(classOf[InMemoryCarStorageService])
      case false =>
        bind(classOf[CarStorageService]).to(classOf[DynamoDbCarStorageService])
    }
  }

}
