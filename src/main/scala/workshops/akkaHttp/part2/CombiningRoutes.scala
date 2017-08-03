package workshops.akkaHttp.part2

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import workshops.Utils.{performRequestWithLogging, printWithHeader}

import scala.collection.immutable.Seq

object CombiningRoutes {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route1 = path("route1"){
      get {
        complete("path from route 1")
      }
    } ~ path("other"){
      get {
        complete("route 1 other")
      }
    }

    val route2 = path("route2"){
      get {
        complete("path from route 2")
      }
    } ~
    path("other"){
      get {
        complete("route 2 other")
      }
    }

    val routes = route1 ~ route2

    val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1"))

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route2"))

    // other from route 1 or route 2?
    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/other"))

    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
