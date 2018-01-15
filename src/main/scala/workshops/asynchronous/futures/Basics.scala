package workshops.asynchronous.futures
import workshops.Utils._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

object Basics {
  import scala.concurrent.ExecutionContext.Implicits.global // dont use in production code

  def main(args: Array[String]): Unit = {
    val futureFromValue: Future[Int] = Future.apply(1)(global) //as in scala it can be written as Future(1)
    val futureFromFunction: Future[String] = functionWhichResturnsAFuture(42)

    printWithHeader("Future from value and from function")
    println(Await.result(futureFromValue, 3 seconds))
    println(Await.result(futureFromFunction, 3 seconds))

    val futureWhichWIllFail = functionWhichThrowsExceptionInFuture // Exception not thrown here

    printWithHeader("What happens when future fails")
    try{
      awaitResult(futureWhichWIllFail)
    } catch {
      case ex: Throwable => println(s"But exception is thrown here $ex")
    }



    val instantFuture: Future[Int] = Future.successful(1)
    val instantFutureException: Future[String] = Future.failed(new Exception("an exception"))

    printWithHeader("Execute function only if some elements are passed")
    println(doComputationIfNeeded(Seq.empty))
    println(doComputationIfNeeded(Seq(5,4,3)))

    printWithHeader("Future from try")
    println(Future.fromTry(Try(1/0)))

  }

  def functionWhichResturnsAFuture(someValue: Int): Future[String] = Future{
    Thread.sleep(1000)
    someValue.toString   // show what will happen if we move Future apply just here
  }


  def functionWhichThrowsExceptionInFuture: Future[Double] = Future{
    throw new IllegalArgumentException("(งಠ_ಠ)ง　σ( •̀ ω •́ σ)")
  }

  def doComputationIfNeeded(numbers: Seq[Int]): Future[Int] ={
    if(numbers.isEmpty) Future.successful(0)
    else Future(numbers.sum / numbers.length)
  }
}
