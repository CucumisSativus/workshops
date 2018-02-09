package workshops.introToScala

object BBIntoToFP {
  //partial application
  def main(args: Array[String]): Unit = {
    def twoArgMethod(firstArg: String)(secondArg: Int): String = {
      s"this is first arg $firstArg this is second arg $secondArg"
    }

    val twoArgFunction: String => Int => String = firstArg => secondArg =>
      s"this is first arg $firstArg this is second arg $secondArg"

    val partiallyAppliedMethod: Int => String = twoArgMethod("firstArg")
    val partialLyAppliedFunction: Int => String = twoArgFunction("firstArg")


    def threeArgsMethod(arg1: String, arg2: Int, arg3: Double): String = {
      s"$arg1 $arg2 $arg3"
    }

    val wootIsThis: Int => String = threeArgsMethod("arg1", _, 1.0)
    val wootIsThis2: (String, Double) => String = threeArgsMethod(_, 10, _)
  }


}
