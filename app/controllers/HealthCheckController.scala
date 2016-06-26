package controllers

import com.google.inject.Singleton
import play.api.mvc.{Action, Controller}

class HealthCheckController extends Controller {

  def isAlive() = Action {
    Ok("Alive")
  }

}
