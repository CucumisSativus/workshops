package workshops.asynchronous.monix

import java.util.concurrent.Executors

import monix.execution.atomic.AtomicLong
import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import workshops.Utils._

import scala.concurrent.duration._
import monix.execution.Scheduler.global
import monix.execution.{Scheduler, UncaughtExceptionReporter}
import monix.execution.UncaughtExceptionReporter.LogExceptionsToStandardErr
import monix.execution.schedulers.{AsyncScheduler, ThreadFactoryBuilder}

import scala.concurrent.ExecutionContext
import workshops.Utils._
object CCAsyncBoundaries {
  def main(args: Array[String]): Unit = {

    printWithHeader("No async boundaries")
    val taskWithoutAsyncBoundaries = Task(runFunction(1))
      .map(runFunction)
      .map(runFunction)
      .map(runFunction)

    println(awaitResult(taskWithoutAsyncBoundaries.runAsync(Scheduler.singleThread("a-thread"))))

    printWithHeader("With async boundaries")
    val taskWithAsyncBoundaries =
      Task(runFunction(1))
        .asyncBoundary(Scheduler.singleThread("thread1"))
      .map(runFunction)
      .map(runFunction)
      .asyncBoundary(Scheduler.singleThread("thread2"))
      .map(runFunction)

    println(awaitResult(taskWithAsyncBoundaries.runAsync(Scheduler.singleThread("thread3"))))
  }

  def runFunction(input: Int) = {
    println(s"Running on ${Thread.currentThread().getName}")
    input +1
  }

}
