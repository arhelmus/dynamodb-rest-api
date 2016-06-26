package utils

sealed trait SortingDirection
case object ASC extends SortingDirection
case object DESC extends SortingDirection

object SortingDirection {

  def parse(sortDirection: String) =
    sortDirection.toUpperCase() match {
      case "ASC" => Some(ASC)
      case "DESC" => Some(DESC)
      case _ => None
    }

  def isSortedBy[Obj, F](data: Seq[Obj], sortedBy: Obj => F, direction: SortingDirection): Boolean = {

    def isCorrectOrder(firstField: F, secondField: F): Boolean =
      direction match {
        case ASC => firstField.toString <= secondField.toString
        case DESC => firstField.toString >= secondField.toString
      }

    data.foldLeft(true -> data.head) {
      case ((true, prevObj), obj) => isCorrectOrder(sortedBy(prevObj), sortedBy(obj)) -> obj
      case ((false, _), obj) => false -> obj
    }._1

  }

  def sortBy[Obj, F](data: Seq[Obj], sortedBy: Obj => F, direction: SortingDirection): Seq[Obj] =
    direction match {
      case ASC => data.sortWith((c1, c2) => sortedBy(c1).toString < sortedBy(c2).toString)
      case DESC => data.sortWith((c1, c2) => sortedBy(c1).toString > sortedBy(c2).toString)
    }

}