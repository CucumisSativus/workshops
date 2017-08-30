package workshops

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
object Utils {
  private val defaultTimeout = 4 seconds

  def awaitResult[T](f: Future[T]): T = Await.result(f, defaultTimeout)

  def performRequest(httpMethod: HttpMethod, url: String, requestEntity: RequestEntity = HttpEntity.Empty)
                    (implicit mat: Materializer, ac: ActorSystem, ec: ExecutionContext): String = {
    performRequest(HttpRequest(method = httpMethod, uri = url, entity = requestEntity))
  }

  def performRequest(request: HttpRequest)
                    (implicit mat: Materializer, ac: ActorSystem, ec: ExecutionContext): String = {
    val readResponseBody = (r: HttpResponse) => r.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    awaitResult(Http().singleRequest(request).flatMap(readResponseBody))
  }

  def performRequestWithLogging(request: HttpRequest)
                               (implicit mat: Materializer, ac: ActorSystem, ec: ExecutionContext): String = {
    val uri = request.uri.toString()
    printWithHeader(s"${request.method.value} $uri")
    performRequest(request)
  }
  def performRequestWithLogging(httpMethod: HttpMethod, url: String, requestEntity: RequestEntity = HttpEntity.Empty)
                               (implicit mat: Materializer, ac: ActorSystem, ec: ExecutionContext): String = {
    printWithHeader(s"${httpMethod.value} $url")
    performRequest(httpMethod, url, requestEntity)
  }


  def printWithHeader(message: String): Unit ={
    println()
    println("********************************************************")
    println(s">>$message<<")
    println("********************************************************")
    println()
  }
}
