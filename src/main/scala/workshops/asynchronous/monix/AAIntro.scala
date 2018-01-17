package workshops.asynchronous.monix


import monix.execution.CancelableFuture

import scala.concurrent.{ExecutionContext, Future}

// Task is in monix.eval
import monix.eval.Task
import scala.util.{Success, Failure}
import scala.concurrent.duration._
import workshops.Utils._

object AAIntro {
  // Instead of scala.concurrent.ExecutionContext
  import monix.execution.Scheduler.Implicits.global
  /**
    * Monix scheduler vs scala execution context:
    * - trait Scheduler extends ExecutionContext
    * - have possibility to execute things with delay
    * - have possibility to execute things periodically
    * - have possibility to cancel already run things
    * - can be compared to Akka scheduler but without need to use akka as dependency
    */

  def main(args: Array[String]): Unit = {
    val firstTask = Task.apply(1) // note that no implicit is required yet!

    val result: CancelableFuture[Int] = firstTask.runAsync(global) //scheduler is required only when running task


    printWithHeader("Cancellable Future")
    val veryLongTask: Task[Int] = Task{
      Thread.sleep(300)
      println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
      println("!!!!!!!we should never get there!!!!!!!!!")
      println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
      42
    }.asyncBoundary

    val cancellable = veryLongTask.runAsync

    Thread.sleep(100)
    println("Cancelling cancellable future")
    cancellable.cancel()

    printWithHeader("Delayed execution")

    val startTime = System.currentTimeMillis()

    val task = Task{
      val currentTime = System.currentTimeMillis()
      println(s"Delayed execution finished after ${currentTime - startTime} milis")
    }.delayExecution(100 milli)

    awaitResult(task.runAsync)

    printWithHeader("Liting values to task")

    val successfulTask: Task[String] = Task.now("value")
    val failedTask: Task[String] = Task.raiseError(new Exception("exception"))


    printWithHeader("Feeling lucky")
    val alreadyFinisedhTask = Task.now("finished")
    val runningTask = Task{ Thread.sleep(100); 50}

    println("Finished task")
    println(alreadyFinisedhTask.coeval.value)

    println("async task which not started yet")
    println(runningTask.coeval.value)

    val runningTaskFuture = runningTask.runAsync
    println("async task finished - future value")
    println(awaitResult(runningTaskFuture))
    println("async task finished - coeval value")
    println(runningTask.coeval.value)


    printWithHeader("Cancellable one more time")
    val thisWillBeCancelled = Task{
      println("For sure you wont see this")
    }.delayExecution(10 milli)

    thisWillBeCancelled.runAsync.cancel()

    Thread.sleep(100)


    printWithHeader("Memoization")
    val future = Future{
      println("Future started!")
      42
    }

    emptyLine(3)
    println(s"future value 1 ${awaitResult(future)}")
    println(s"future value 2 ${awaitResult(future)}")


    val notmalTask = Task{
      println("Starting task!")
      42
    }

    emptyLine(3)
    println(s"task value 1 ${awaitResult(notmalTask.runAsync)}")
    println(s"task value 2 ${awaitResult(notmalTask.runAsync)}")

    val momoizedTask = Task.evalOnce{
      println("Starting memoized task")
      42
    }

    emptyLine(3)
    println(s"memoized task value 1 ${awaitResult(momoizedTask.runAsync)}")
    println(s"memoized task value 2 ${awaitResult(momoizedTask.runAsync)}")

    printWithHeader("Task from future")

     val taskFromFuture = Task.fromFuture{
       Future{
         "hello and welcome"
       }
     }

    println(awaitResult(wrappedRepositoryMethod("length").runAsync))
  }

  def repostioryMethod(argument: String)(implicit ec: ExecutionContext): Future[Int] = Future{
    argument.length
  }

  def wrappedRepositoryMethod(argument: String): Task[Int] =
    Task.deferFutureAction(
      scheduler =>
        repostioryMethod(argument)(scheduler)
    )
}
