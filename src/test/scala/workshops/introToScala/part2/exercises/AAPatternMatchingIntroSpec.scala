package workshops.introToScala.part2.exercises

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
  }
}
