import com.google.inject.AbstractModule
import play.api.{Configuration, Environment, Mode}
import services.{CarStorageService, DynamoDbCarStorageService, InMemoryCarStorageService, StaticCarStorageService}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure() = {
    environment.mode match {
      case Mode.Test =>
        bind(classOf[CarStorageService]).to(classOf[StaticCarStorageService])
      case Mode.Dev =>
        bind(classOf[CarStorageService]).to(classOf[InMemoryCarStorageService])
      case Mode.Prod =>
        bind(classOf[CarStorageService]).to(classOf[DynamoDbCarStorageService])
    }
  }

}
