package workshops.introToScala.part2
import workshops.Utils._
object BBMoreAdvancedPatternMatching {
  def main(args: Array[String]): Unit = {

    printWithHeader("Using apply to create fancy constructor")
    class AnOldFashionedClass(argument: String){
      def shout: String = argument + "!"
    }

    object AnOldFashionedClass{
      def apply(argument: String): AnOldFashionedClass = new AnOldFashionedClass(argument)
    }

    val instance1 = new AnOldFashionedClass("argument")
    val instance2 = AnOldFashionedClass("argument2")

    printWithHeader("Turning simple class into almost case class")
    class SimpleClass(initialState: Int){
      private var state = initialState

      def increment() : Unit = state += 1
      def decrement(): Unit = state -= 1
    }

    object SimpleClass{
      def unapply(arg: SimpleClass): Option[Int] = Some(arg.state)
      def apply(initialState: Int): SimpleClass = new SimpleClass(initialState)
    }


    val instance = SimpleClass(1) //Note this syntax!
    instance.increment()
    instance.increment()

    val result = instance match {
      case SimpleClass(res) => res
    }

    println(result)

    // ask why 'almost' case classes

    printWithHeader("Extractor pattern")

    object EuropeanAgeExtractor{
      def unapply(arg: Int): Option[String] = if(arg >= 18) Some("Europe!") else None
    }

    object IsAbleToDrinkInAmerica{
      def unapply(arg: Int): Boolean = arg >= 21
    }

    def isAbleToDring(age: Int): String = age match {
      case IsAbleToDrinkInAmerica() => "America!"
      case EuropeanAgeExtractor(str) => str
      case _ => "too young"
    }

    printWithHeader(isAbleToDring(17))
    printWithHeader(isAbleToDring(18))
    printWithHeader(isAbleToDring(21))


    printWithHeader("Sealed trait as just enum")

    sealed trait ScriptSortType
    case object Text extends ScriptSortType
    case object Number extends ScriptSortType

    case class Script(value: String, sortyType: ScriptSortType)

    def executeScript(script: Script) = script match {
      case Script(value, Text) => "sorting as text"
      case Script(value, Number) => "sorting as number"
    } // show what would happen if we remove one of the matches



    printWithHeader("Sealed trait as algebraic data type")

    sealed trait DomainMessage
    case object EmptyMessage extends DomainMessage
    case class NumberMessage(num: Int) extends DomainMessage
    case class StringMessage(str: String) extends DomainMessage


    def handleMessage(msg: DomainMessage): String = msg match {
      case EmptyMessage => "Empty message not doing anything"
      case NumberMessage(num) => s"Number message $num"
      case StringMessage(str) => s"String message $str"
    }

    println(handleMessage(EmptyMessage))
    println(handleMessage(NumberMessage(42)))
    println(handleMessage(StringMessage("a string")))
  }
}
