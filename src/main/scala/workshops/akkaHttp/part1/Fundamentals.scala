package workshops.akkaHttp.part1

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import workshops.Utils.{performRequest, printWithHeader}

import scala.collection.immutable.Seq

object Fundamentals {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route = path("route"){
      get{
        complete(HttpResponse(
          status = StatusCodes.OK,
          headers = Seq[HttpHeader](),
          entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "content"),
          protocol = HttpProtocols.`HTTP/1.1`
        )
        )
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    printWithHeader("performing get request")
    println(performRequest(HttpMethods.GET, "http://localhost:8080/route"))

    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
