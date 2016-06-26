package utils

sealed trait SortingDirection
case object ASC extends SortingDirection
case object DESC extends SortingDirection

object SortingDirection {

  def isSortedBy[Obj, F](data: Iterable[Obj], sortedBy: Obj => F, direction: SortingDirection): Boolean = {

    def isCorrectOrder(firstField: F, secondField: F): Boolean =
      direction match {
        case ASC => firstField.toString >= secondField.toString
        case DESC => secondField.toString <= firstField.toString
      }

    data.foldLeft(true -> data.head) {
      case ((true, prevObj), obj) => isCorrectOrder(sortedBy(prevObj), sortedBy(obj)) -> obj
      case ((false, _), obj) => false -> obj
    }._1

  }

}