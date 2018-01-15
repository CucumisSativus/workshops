package workshops.asynchronous.futures

import scala.concurrent.Future
import workshops.Utils._
import scala.concurrent.ExecutionContext.Implicits.global
object FutureTraps {
  def main(args: Array[String]): Unit = {
    def methodWhichRetrievesAFuture(a: Int): Future[Long] = Future{
      Thread.sleep(1000)
      System.currentTimeMillis()
    }

    def composeStuff(): Future[Long] = {
      val fa = methodWhichRetrievesAFuture(1)
      val fb = methodWhichRetrievesAFuture(2)

      for {
        a <- fa
        b <- fb
      } yield b -a
    }

    def composeStuff2(): Future[Long] = {
      for {
        a <- methodWhichRetrievesAFuture(1)
        b <- methodWhichRetrievesAFuture(2)
      } yield b-a
    }

    println(awaitResult(composeStuff()))
    println(awaitResult(composeStuff2()))
  }
}
