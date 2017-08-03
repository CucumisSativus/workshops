package workshops

import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
class UnitSpec extends WordSpec with MustMatchers{
  def futureResults[T](f: Future[T]): T = Await.result(f, 5 seconds)
}
