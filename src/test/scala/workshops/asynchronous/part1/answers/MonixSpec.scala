package workshops.asynchronous.part1.answers

import monix.eval.Task
import monix.execution.atomic.AtomicLong
import org.scalatest.concurrent.ScalaFutures
import workshops.UnitSpec
import workshops.asynchronous.part1.answers.MonixSpec.MyRequestService
import scala.concurrent.duration._
class MonixSpec extends UnitSpec with ScalaFutures{
  import monix.execution.Scheduler.Implicits.global

  "monix tasks" should {
    "compose two tasks" in {
      whenReady(service.composeTask(10).runAsync){ result =>
        result mustBe "11"
      }
    }

    "start a task after some time" in {
      whenReady(service.delayedTask.runAsync){ result =>
        result mustBe 42
      }
    }

    "restart a task in case of not expected value" in {
      whenReady(service.retryTask.runAsync){ result =>
        result mustBe 42
      }
    }

    "takes first of list of tasks" in {
      whenReady(service.takeFirstOfTasks.runAsync){ result =>
        result mustBe 42
      }
    }
  }

  def service = new MyRequestService
}

private[answers] object MonixSpec{
  class MyRequestService{
    val testService = new TestService

    def delayedTask: Task[Int] = Task{testService.methodWhichReturnProperValueAfterSomeTime}.delayExecution(100 milli) //testService.methodWhichReturnProperValueAfterSomeTime
    def retryTask: Task[Int] = Task(testService.methodWhichReturnsProperValueAfterSecondCall).restartUntil(_ == 42) //testService.methodWhichReturnsProperValueAfterSecondCall
    def composeTask(value: Int): Task[String] = testService.taskToBeComposed1(value).flatMap(testService.taskToBeComposed2) // compose taskToBeComposed1 -> taskToBeComposed2
    def takeFirstOfTasks: Task[Int] = {
      val tasks = testService.methodRetuningListOfTasks
      Task.chooseFirstOfList(tasks)
    }
  }


  class TestService{
    private val startTime = System.currentTimeMillis()

    val callCounter = AtomicLong(0)

    def methodWhichReturnProperValueAfterSomeTime: Int ={
      if(isAfterTime(startTime, 100)) 42
      else 0
    }

    def methodWhichReturnsProperValueAfterSecondCall: Int = {
      if(callCounter.getAndIncrement() >= 1) 42
      else 0
    }

    def taskToBeComposed1(value: Int): Task[Int] = Task.now(value + 1)
    def taskToBeComposed2(value: Int): Task[String] = Task.now(value.toString)
    def methodRetuningListOfTasks: List[Task[Int]] = List(
      Task.now(5).delayExecution(100 milli),
      Task.now(42).delayExecution(50 milli),
      Task.now(10).delayExecution(300 milli)
    )
  }

  def isAfterTime(startTime: Long, duration: Long): Boolean = System.currentTimeMillis() - startTime > duration
}