package workshops.introToScala.answers

import workshops.UnitSpec

class IntroToFPSpec extends UnitSpec{
  "functional programming adept" should {
    "be able to partially apply a method" in {
      def methodWithTwoArguments(base: Int)(toAdd: Int): Int = {
        base + toAdd
      }

      val addTwo: Int => Int = methodWithTwoArguments(2)

      addTwo(2) mustBe 4
    }

    "be able to partially apply a method which is not prepared to this (with _)" in {
      def addNumbers(number1: Int, number2: Int): Int = {
        number1 + number2
      }

      val addThree: Int => Int = addNumbers(3, _)

      addThree(2) mustBe 5

    }

    "be able to add an element at the end of the list" in {
      val baseList = List(1,2,3)

      val newList = baseList :+ 4

      newList mustBe List(1,2,3,4)
    }

    "be able to add an element in front of the list" in {
      val baseList = List(1,2,3)

      val newList = 0 +: baseList

      newList mustBe List(0, 1,2,3)
    }

    "be able to get head and tail of the list" in {
      val list = List(1,2,3,4)

      val head = list.head
      val tail = list.tail

      head mustBe 1
      tail mustBe List(2,3,4)
    }

    "be able to filter even number from the list" in {
      val list = List(1,2,3,4,5,6,7,8)

      val evenNumbers = list.filter(_ % 2 == 0)

      evenNumbers mustBe List(2,4,6,8)
    }

    "be able to increase every element in the list by 1" in {
      val list = List(1,2,3,4,5)

      val increased = list.map(_ +1)

      increased mustBe List(2,3,4,5,6)
    }

    "be able to pass partially applied function to map" in {
      val list = List(1,2,3,4)

      def increaseBy(by: Int, element: Int): Int = {
        element + by
      }

      val newList = list.map(increaseBy(3, _))
      newList mustBe List(4,5,6,7)
    }

    "be able to triple element if its even and remove it if odd" in {
      val list = List(1,2,3,4,5,6)

      def tripleElementIfEven(el: Int): List[Int] = {
        if(el % 2 == 0) List(el, el, el)
        else List()
      }

      val newList = list.flatMap(tripleElementIfEven)

      newList mustBe List(2,2,2, 4,4,4, 6,6,6)
    }

    "be able to find a user with given name" in {
      case class User(id: Int, email: String)

      val users = List(
        User(1, "email1@example.com"),
        User(2, "email2@example.com")
      )

      val foundUser = users.find(_.email == "email1@example.com")
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
        users.find(_.id == id).map(user => Email(user.email))
      }

      extractEmail(users)(1) mustBe Some(Email("email1@example.com"))
      extractEmail(users)(5) mustBe None
    }

  }
}
