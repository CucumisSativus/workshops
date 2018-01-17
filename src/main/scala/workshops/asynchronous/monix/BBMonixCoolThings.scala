package workshops.asynchronous.monix

import java.util.concurrent.TimeoutException

import monix.execution.atomic.AtomicLong
import monix.eval.Task
import workshops.Utils._

import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global
object BBMonixCoolThings {
  def main(args: Array[String]): Unit = {
    printWithHeader("Conecting tasks together")
    val task1 = Task.now(12)
    def task2(value: Int) = Task.now(value +1)

    val conectedTask = task1.flatMap(task2)

    println(awaitResult(conectedTask.runAsync))

    println("Connecting tasks with pure functions")
    val addSomething = (value: Int) => value +1
    Task.now(12).map(addSomething)

    println()
    printWithHeader("Retires")

    val callCounter = AtomicLong(1)
    def methodToBeRetired = {
      println("entering retry loop")
      if(callCounter.getAndIncrement() >= 3) 42
      else 0
    }

    val taskWithRetries =
      Task(methodToBeRetired)
        .delayExecution(50 milli)
        .restartUntil(_ == 42)

    println(awaitResult(taskWithRetries.runAsync))

    printWithHeader("Task timeout")
    val timeoutableTask = taskWithRetries.timeout(50 milli)

    try{
      awaitResult(timeoutableTask.runAsync)
    } catch{
      case e: TimeoutException => println("Task timeouted!")
    }

    printWithHeader("Exponential backoff")
    val callCounter2 = AtomicLong(1)
    val startTime = System.currentTimeMillis()
    def methodToBeRetiredExponentially = {
      val currentTIme = System.currentTimeMillis()
      println(s"entering retry loop time elapsed ${currentTIme - startTime}")
      if(callCounter2.getAndIncrement() >= 3) 42
      else throw new Exception("should retry!")
    }

    def retryBackoff[A](source: Task[A],
                        maxRetries: Int, firstDelay: FiniteDuration): Task[A] = {

      source.onErrorHandleWith {
        case ex: Exception =>
          if (maxRetries > 0)
          // Recursive call, it's OK as Monix is stack-safe
            retryBackoff(source, maxRetries-1, firstDelay*2)
              .delayExecution(firstDelay)
          else
            Task.raiseError(ex)
      }
    }

    val taskToBeRetries = retryBackoff(Task(methodToBeRetiredExponentially), 5, 10 milli)
    println(awaitResult(taskToBeRetries.runAsync))

  }
}
