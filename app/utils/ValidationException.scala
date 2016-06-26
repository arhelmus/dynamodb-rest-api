package utils

import com.wix.accord.Violation

case class ValidationException(violations: Set[Violation]) extends RuntimeException(ValidationException.exceptionMessage(violations))
object ValidationException {
  def exceptionMessage(violations: Set[Violation]) =
    "Errors occurred during validation process: " +
      violations.map(v => v.description.getOrElse("Field") + " " + v.constraint).mkString(", ")
}
