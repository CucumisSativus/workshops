package workshops.introToScala.part2.answers

import workshops.UnitSpec

class AAPatternMatchingIntroSpec extends UnitSpec{
  "a scala adept" should {
    "be able to use pattern matching as a switch" in {
      def patternMatchingAsSwitch(arg: Int): String = arg match {
        case 5 => "five!"
        case 3 => "three!"
        case _ => "something else!"
      }

      patternMatchingAsSwitch(5) mustBe "five!"
      patternMatchingAsSwitch(3) mustBe "three!"
      patternMatchingAsSwitch(12) mustBe "something else!"
      patternMatchingAsSwitch(2) mustBe "something else!"
    }

    "be able to deconstruct case class" in {
      case class User(name: String, age: Int)

      def greetUser(user: User): String = user match {
        case User("Admin", _) => "Admin super user!"
        case User(name, age) if age > 18 => s"Hello $name"
        case User(name, age) => s"$name is too young"
      }

      greetUser(User("Whitfield Diffie", 74)) mustBe "Hello Whitfield Diffie"
      greetUser(User("Martin Hellman", 73)) mustBe "Hello Martin Hellman"
      greetUser(User("Random Guy", 12)) mustBe "Random Guy is too young"
      greetUser(User("Admin", 2)) mustBe "Admin super user!"
    }

    "be able to obtain data from nested case classes" in {
      case class User(name: String)
      case class Item(name: String, price: Int)
      case class Order(user: User, item: Item)

      def getPriceForUser(order: Order): String = order match {
        case Order(User(name), Item(_, price)) => s"$name - $price"
      }

      getPriceForUser(Order(User("Margaret Hamilton"), Item("Apollo rocket", 120))) mustBe "Margaret Hamilton - 120"
      getPriceForUser(Order(User("Peter G. Neumann"), Item("Architecture book", 300))) mustBe "Peter G. Neumann - 300"

    }

    "be able to pattern match on a list" in {

      def canBeATriangle(list: List[Int]): String = list match{
        case first :: second :: last :: Nil if last < (first + second) => "Can be a triangle!"
        case first :: second :: last :: Nil if last >= (first + second) => s"The longest side - $last is too short"
        case l if l.length < 3 => "Not enough elements to check"
        case l if l.length > 3 => "To many sides for a triangle!"
      }

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
        triangles.filter{ case Triangle(first, second, longest) =>
          longest < (first + second)
        }
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
