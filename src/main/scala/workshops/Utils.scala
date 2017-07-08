package workshops

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethod, HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
object Utils {
  private val defaultTimeout = 4 seconds

  def awaitResult[T](f: Future[T]): T = Await.result(f, defaultTimeout)

  def performRequest(httpMethod: HttpMethod, url: String)
                    (implicit mat: Materializer, ac: ActorSystem, ec: ExecutionContext): String = {
    val readResponseBody = (r: HttpResponse) => r.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    awaitResult(Http().singleRequest(HttpRequest(method = httpMethod, uri = url)).flatMap(readResponseBody))
  }

  def printWithHeader(message: String): Unit ={
    println()
    println("********************************************************")
    println(s">>$message<<")
    println("********************************************************")
    println()
  }
}
