package workshops.introToScala

object AAIntro {
  def main(args: Array[String]): Unit = {
    val aValue: Int = 1 // value, constant in other programming languages
    // aValue = 2 <--- this will not compile

    var aVariable: String = "a string"
    aVariable = "another string"
    // aVariable = 5 <-- this will not compile since types does not match

    val anotherValue = 10 // notice no type here, compiler already knows what tu choose


    val function: Int => String = { argument => argument.toString } // function which transforms integer into string

    val result: String = function(5) // shorter syntax for function.apply(5)

    def aMethod(arg1: String, arg2: Double): String = s"$arg1 - ${arg2.toString}"

    aMethod("arg", 1.0d)
    aMethod(arg1 = "arg", arg2 = 1.0d) //named parameters

    def abitLongerMethod(arg1: Int): String = {
      if (arg1 > 0) "Negative"
      else "nonNegative" //comment this line and see that this wont compile!
    }

    println("Welcome to scala!")

    def methodWhichAcceptsAFunctionAsParameter(f: Int => String, arg: Int): String = {
      f(arg)
    }

    def aMethodWhichReturnsAFunction: Double => Int = {
      a: Double => a.toInt
    }

    /*
    >> Classes <<
     */
    class Class1(constructorArg1: String, constructorArg2: Int) {
      // this is constructor body :O
      private val aClassVariable = constructorArg2.toString

      def classMethod1 = constructorArg1
    }

    val newInstance1 = new Class1("arg1", 1)
    newInstance1.classMethod1


    class Class2(val constructorArg: String) {
      def method: String = s"This is constructor arg $constructorArg"
    }

    val newInstance2 = new Class2("arg")
    newInstance2.method
    newInstance2.constructorArg // <-- getter included!

    class Class3(var constructorArg: String)

    val newInstance3 = new Class3("arg")
    newInstance3.constructorArg //getter
    newInstance3.constructorArg = "new value" //setter


    trait Animal { // interface
      def makeNoise: String
    }

    trait WithLegs {
      def move: Unit // Unit is void in most of languages
    }

    class Dog extends Animal with WithLegs {
      override def makeNoise: String = "Bark"

      override def move: Unit = {
        println("I'm moving")
      }
    }

    class SomeClass(arg: String)
    class ExtendedClass extends SomeClass("arg")

    object ASingleton { //an object is a nice place to organize your functions
      def aMethodFromSingleton: Int = { 42 }
    }

    ASingleton.aMethodFromSingleton

    case class ACaseClass(field1: String, field2: Int)

    val caseClassInstance = ACaseClass("field1", 2) // notice no *new* !

    caseClassInstance.field1
    caseClassInstance.field2

    val newCaseClassInstance = caseClassInstance.copy(field2 = 3)
  }
}
