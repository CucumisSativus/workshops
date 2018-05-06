package workshops.introToScala.part2.exercises

import workshops.UnitSpec

class AAPatternMatchingIntroSpec extends UnitSpec{
  "a scala adept" should {
    "be able to use pattern matching as a switch" in {
      def patternMatchingAsSwitch(arg: Int): String = ???

      patternMatchingAsSwitch(5) mustBe "five!"
      patternMatchingAsSwitch(3) mustBe "three!"
      patternMatchingAsSwitch(12) mustBe "something else!"
      patternMatchingAsSwitch(2) mustBe "something else!"
    }

    "be able to deconstruct case class" in {
      case class User(name: String, age: Int)

      def greetUser(user: User): String = ???

      greetUser(User("Whitfield Diffie", 74)) mustBe "Hello Whitfield Diffie"
      greetUser(User("Martin Hellman", 73)) mustBe "Hello Martin Hellman"
      greetUser(User("Random Guy", 12)) mustBe "Random Guy is too young"
      greetUser(User("Admin", 2)) mustBe "Admin super user!"
    }

    "be able to obtain data from nested case classes" in {
      case class User(name: String)
      case class Item(name: String, price: Int)
      case class Order(user: User, item: Item)

      def getPriceForUser(order: Order): String = ???

      getPriceForUser(Order(User("Margaret Hamilton"), Item("Apollo rocket", 120))) mustBe "Margaret Hamilton - 120"
      getPriceForUser(Order(User("Peter G. Neumann"), Item("Architecture book", 300))) mustBe "Peter G. Neumann - 300"

    }

    "be able to pattern match on a list" in {

      def canBeATriangle(list: List[Int]): String = ???

      // for simplicity the last element is the longest one
      canBeATriangle(List()) mustBe "Not enough elements to check"
      canBeATriangle(List(1)) mustBe "Not enough elements to check"
      canBeATriangle(List(1,2)) mustBe "Not enough elements to check"
      canBeATriangle(List(1,2,3)) mustBe "The longest side - 3 is too short"
      canBeATriangle(List(2,3,4)) mustBe "Can be a triangle!"
      canBeATriangle(List(1,2,3,4)) mustBe "To many sides for a triangle!"
      canBeATriangle(List(1,1,2,3,5)) mustBe "To many sides for a triangle!"
    }

    "be able to pattern match in map" in {
      case class Triangle(first: Int, second: Int, longest: Int)

      def getValidTriangles(triangles: List[Triangle]): List[Triangle] = {
        ???
      }

      val list = List(
        Triangle(1,1,1),
        Triangle(1,2,3),
        Triangle(3,4,5),
        Triangle(1,1,4),
        Triangle(5,6,7)
      )

      val expected = List(
        Triangle(1,1,1),
        Triangle(3,4,5),
        Triangle(5,6,7)
      )


      getValidTriangles(list) mustBe expected
    }
  }
}
