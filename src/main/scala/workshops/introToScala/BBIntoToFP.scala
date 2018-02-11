package workshops.introToScala

object BBIntoToFP {
  import workshops.Utils._
  def main(args: Array[String]): Unit = {
    //partial application
    printWithHeader("Partial application")
    def twoArgMethod(firstArg: String)(secondArg: Int): String = {
      s"this is first arg $firstArg this is second arg $secondArg"
    }

    val twoArgFunction: String => Int => String = firstArg => secondArg =>
      s"this is first arg $firstArg this is second arg $secondArg"

    val partiallyAppliedMethod: Int => String = twoArgMethod("firstArg")
    val partialLyAppliedFunction: Int => String = twoArgFunction("firstArg")

    println(s"partially applied method ${partiallyAppliedMethod(2)}")
    println(s"partially applied function ${partiallyAppliedMethod(2)}")

    def threeArgsMethod(arg1: String, arg2: Int, arg3: Double): String = {
      s"first arg $arg1 second arg $arg2 third arg $arg3"
    }


    val wootIsThis: Int => String = threeArgsMethod("arg1", _, 3.0)
    val wootIsThis2: (String, Double) => String = threeArgsMethod(_, 2, _)

    println(s"wootIsThis ${wootIsThis(2)}")
    println(s"wootIsThis2 ${wootIsThis2("arg1", 3.0)}")


    // immutable collections

    printWithHeader("Immutable collections - list")

    val aList = List(1,2,3)
    val newList = aList :+ 4
    println(s"aList - $aList newList - $newList")
    val head: Int = newList.head // 1

    println(s"newList head - $head newList $newList")

    val tail = newList.tail // List(2,3,4)

    println(s"newList tail $tail, newList $newList")

    val filterFunction : Int => Boolean = arg => arg > 2

    val filteredList = newList.filter(filterFunction)

    println(s"Filtered collection $filteredList")

    val mapFunction : Int => Int = arg => arg *2

    val mappedList = newList.map(mapFunction)

    println(s"mapped list $mappedList")

    val flatMapFunction : Int => List[Int] = arg => List(arg, arg)

    val flatMappedList = newList.flatMap(flatMapFunction)

    println(s"Flat mapped list $flatMappedList")

    // optionals

    printWithHeader("Optionals")

    case class User(firstName: String,  email: Option[String])

    val userWithEmail = User("name", Some("email@domain.com"))
    val userWithoutEmail = User("name", None)

    println(s"User with email $userWithEmail User without email $userWithoutEmail")

    def sendEmail(email: String): Unit = println(s"sending email to $email")

    userWithEmail.email.foreach(sendEmail)
    userWithoutEmail.email.foreach(sendEmail)

    val userWithEmailEmailLength = userWithEmail.email.map(_.length)
    val userWithoutEmailEmailLength = userWithoutEmail.email.map(_.length)

    val userWithInvalidEmail = User("name1", Some("not_valid"))

    val getDomainFromValidEmail: String => Option[String] = arg =>
      if(arg.contains('@')) Some(arg.split("@").last)
      else None

    val firstUserEmailDomain = userWithEmail.email.flatMap(getDomainFromValidEmail)
    val secondUserEmailDomain = userWithoutEmail.email.flatMap(getDomainFromValidEmail)
    val thirdUserEmailDomain = userWithInvalidEmail.email.flatMap(getDomainFromValidEmail)

    println(s"user with email $userWithEmail domain $firstUserEmailDomain")
    println(s"user without email $userWithoutEmail domain $secondUserEmailDomain")
    println(s"user with invalid email $userWithInvalidEmail domain $thirdUserEmailDomain")
  }


}
