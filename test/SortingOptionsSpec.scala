import org.scalatest.{Matchers, WordSpec}
import utils.{ASC, DESC}

class SortingOptionsSpec extends WordSpec with Matchers {

  import utils.SortingDirection._

  "Sorting options" should {

    "sort by field ascendant" in {
      sortBy[Int, Int](Seq(3, 2, 1), v => v, ASC) should be(Seq(1, 2, 3))
    }

    "sort by field descendant" in {
      sortBy[Int, Int](Seq(1, 2, 3), v => v, DESC) should be(Seq(3, 2, 1))
    }

    "check is sorted ascendant" in {
      isSortedBy[Int, Int](Seq(1, 2, 3), v => v, ASC) should be(true)
      isSortedBy[Int, Int](Seq(3, 2, 1), v => v, ASC) should be(false)
    }

    "check is sorted descendant" in {
      isSortedBy[Int, Int](Seq(1, 2, 3), v => v, DESC) should be(false)
      isSortedBy[Int, Int](Seq(3, 2, 1), v => v, DESC) should be(true)
    }

  }

}
