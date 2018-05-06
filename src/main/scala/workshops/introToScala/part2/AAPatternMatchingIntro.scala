package workshops.introToScala.part2
import workshops.Utils._
object AAPatternMatchingIntro {
  def main(args: Array[String]): Unit = {
    // just a better switch
    def matchAsSwitch(number: Int) = number match {
      case 5 => "five!"
      case 4 | 6 => "four or six!"
      case _ => "not five!"
    }

    printWithHeader("Pattern matching as switch")
    println(matchAsSwitch(5))
    println(matchAsSwitch(4))
    println(matchAsSwitch(6))
    println(matchAsSwitch(7))

    def checkString(string: String): String = { //show how we can omit this ugly brace
      string match {
        case "simple" => "found simple string"
        case str if str.length > 2 => "string has more than 2 characters"
        case str if str.length < 2 => "string has less than 2 characters"
        case "some other string" => "some other string found"
        case str => s"Not expected! Found $str"
      }
    }

    printWithHeader("A little more complex pattern matching")
    println(checkString("simple"))
    println(checkString("long string"))
    println(checkString("s"))
    println(checkString("some other string"))
    println(checkString("very long string"))


    case class User(firstName: String, lastName: String)

    def checkUser(user: User): String = user match {
      case User("Haskell", "Curry") => "Welcome Haskell!"
      case User(firstName, "Dijkstra") => s"Welcome $firstName are you sure that this is your correct first name?"
      case User(firstName, lastName) => s"$firstName $lastName"
    }

    printWithHeader("Pattern matching on case class")
    println(checkUser(User("Haskell", "Curry")))
    println(checkUser(User("first name", "Dijkstra")))
    println(checkUser(User("first name", "last name")))

    printWithHeader("Pattern matching on nested case classed")
    case class Content(message: String)
    case class Postcard(to: User, content: Content)

    def sendPostCard(postcard: Postcard): String = postcard match {
      case Postcard(User(firstName, lastName), Content(message)) => s"Sending postcard to $firstName $lastName with content $message"
    }

    println(sendPostCard(Postcard(User("Alan", "Turing"), Content("wish you were here"))))

    val emptyList = List() // or Nil - do not confuse it with null!
    val oneElementList = List(1)
    val twoElementsList = List(1, 2)
    val threeElementList = List(1,2,3,4)

    def matchList(list: List[Int]): String = list match {
      case List(1,2, x, 4) => s"encountered nice pattern with $x"
      case firstElem :: secondElem :: tail => s"$firstElem $secondElem $tail"
      case head :: tail => s"$head $tail"
      case Nil => "empty list"
    }

    printWithHeader("Pattern matching on lists")
    println(matchList(emptyList))
    println(matchList(oneElementList))
    println(matchList(twoElementsList))
    println(matchList(threeElementList))


    printWithHeader("Matching on type")
    class JustAClassNotCaseOne(val data: Int)

    def matchOnType(msg: Any): String = msg match {
      case s: String => s"Received String $s"
      case c: JustAClassNotCaseOne => s"Received simple class ${c.data}"
      case _ => "Something different"
    }

    println(matchOnType("just a string"))
    println(matchOnType(new JustAClassNotCaseOne(42)))
    println(matchOnType(List(1,2,3)))


    printWithHeader("Matching whole pattern")
    def matchWholePattern(postcard: Postcard): String = postcard match {
      case postcard @ Postcard( user @ User(firstName, lastName), _) => s"$firstName $lastName $user in ${postcard.getClass}"
    }

    println(matchWholePattern(Postcard(User("Adi", "Shamir"), Content("Thanks for RSA!"))))


    printWithHeader("Pattern matching in map")

    val list = List.tabulate(6)(index => User(s"first name $index", s"last name $index"))
    val lastNames = list.map{ case(User(firstName, lastName)) => lastName}
    println(lastNames)

  }
}
