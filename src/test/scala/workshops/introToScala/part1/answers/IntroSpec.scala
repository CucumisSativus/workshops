package workshops.introToScala.part1.answers


import workshops.UnitSpec

class IntroSpec extends UnitSpec {
  "New scala adept" should {
    "be able to define a value" in {
      // declare value "value" of type String with value "i can declare a value!"

      val aValue = "i can declare a value!"
      // uncoment lines below when you are ready to test
      aValue mustBe "i can declare a value!"
    }

    "be able to define a variable" in {
      // declare variable "anInt" of type Int with value 41
      var anInt = 41
      // uncomment lines below when you are ready to test
       anInt mustBe 41
       anInt = anInt +1
       anInt mustBe 42
    }

    "be able to define a function" in {
      // declare a function (this thing with the arrow) named "add" which accepts an int and adds one to it

      val add: Int => Int = { arg => arg + 1 }

      // uncomment lines below when you are ready to test
      add(2) mustBe 3
    }

    "be able to define a method" in {
      // declare a method (with def) called hello which will accept a name (of type string) and return a string "hello $name"

      def hello(name: String) = { s"hello $name" }
      // uncomment lines below when you are ready to test
       hello("Haskell") mustBe "hello Haskell"
    }


    "be able to define a method which accepts functions as arguments" in {
      val handleCorretAnser = (value: String) => s"You are correct. The answer is $value!"
      val handleWrong = (value: String) => s"$value is not a correct answer"

      // create a method called "checkAnswer" which will accept 2 functions and a string (and try to figure out what this function should do basing on tests :) )

      def checkAnswer(onCorrectAnswer: String => String, onWrongAswer: String => String, answer: String) = {
        if(answer == "right") onCorrectAnswer(answer)
        else onWrongAswer(answer)
      }
      // uncomment lines below when you are ready to test
       checkAnswer(handleCorretAnser, handleWrong, "wrong") mustBe "wrong is not a correct answer"
       checkAnswer(handleCorretAnser, handleWrong, "right") mustBe "You are correct. The answer is right!"
    }

    "be able to define a function  which returns a function" in {
      def integerGreaterThanFunction(valueShouldBeGreaterThan: Int) : Int => Boolean = {
        arg: Int => arg > valueShouldBeGreaterThan
      }

      // uncomment lines below when you are ready to test
      val isIntegerAboveThreshold = integerGreaterThanFunction(5)
      isIntegerAboveThreshold(5) mustBe false
      isIntegerAboveThreshold(4) mustBe false
      isIntegerAboveThreshold(6) mustBe true
    }
  }
}

