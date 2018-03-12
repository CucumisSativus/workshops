package workshops.introToScala.exercises

import workshops.UnitSpec

class IntroToFPSpec extends UnitSpec{
  "functional programming adept" should {
    "be able to partially apply a method" in {
      def methodWithTwoArguments(base: Int)(toAdd: Int): Int = {
        base + toAdd
      }

      val addTwo: Int => Int = ???

      addTwo(2) mustBe 4
    }

    "be able to partially apply a method which is not prepared to this (with _)" in {
      def addNumbers(number1: Int, number2: Int): Int = {
        number1 + number2
      }

      val addThree: Int => Int = ???

      addThree(2) mustBe 5

    }

    "be able to add an element at the end of the list" in {
      val baseList = List(1,2,3)

      val newList: List[Int] = ???

      newList mustBe List(1,2,3,4)
    }

    "be able to add an element in front of the list" in {
      val baseList = List(1,2,3)

      val newList : List[Int] = ???

      newList mustBe List(0, 1,2,3)
    }

    "be able to get head and tail of the list" in {
      val list = List(1,2,3,4)

      val head : Int = ???
      val tail : List[Int] = ???

      head mustBe 1
      tail mustBe List(2,3,4)
    }

    "be able to filter even number from the list" in {
      val list = List(1,2,3,4,5,6,7,8)

      val evenNumbers: List[Int] = ???

      evenNumbers mustBe List(2,4,6,8)
    }

    "be able to increase every element in the list by 1" in {
      val list = List(1,2,3,4,5)

      val increased: List[Int] = ???

      increased mustBe List(2,3,4,5,6)
    }

    "be able to pass partially applied function to map" in {
      val list = List(1,2,3,4)

      def increaseBy(by: Int, element: Int): Int = {
        ???
      }

      val newList = list.map(increaseBy(3, _))
      newList mustBe List(4,5,6,7)
    }

    "be able to triple element if its even and remove it if odd" in {
      val list = List(1,2,3,4,5,6)

      def tripleElementIfEven(el: Int): List[Int] = {
        ???
      }

      val newList: List[Int] = list.flatMap(tripleElementIfEven)

      newList mustBe List(2,2,2, 4,4,4, 6,6,6)
    }

    "be able to find a user with given name" in {
      case class User(id: Int, email: String)

      val users = List(
        User(1, "email1@example.com"),
        User(2, "email2@example.com")
      )

      val foundUser: Option[User] = ???
      foundUser mustBe Some(User(1, "email1@example.com"))
    }


    "be able to extract email from user" in {
      case class User(id: Int, email: String)
      case class Email(value: String)

      val users = List(
        User(1, "email1@example.com"),
        User(2, "email2@example.com")
      )

      def extractEmail(users: List[User])(id: Int): Option[Email] ={
        ???
      }

      extractEmail(users)(1) mustBe Some(Email("email1@example.com"))
      extractEmail(users)(5) mustBe None
    }

  }
}
