package workshops.akkaHttp.part1

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import workshops.Utils.performRequest

object UrlParameters {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route: Route =
      path("hello" / Segment) { name =>
        get {
          complete(s"Hello $name")
        }
      } ~
        path("the_answer_to_life_the_universe_and_everything" / LongNumber) { num =>
          get {
            if (num == 42) {
              complete("true")
            } else {
              complete("false")
            }
          }
        } ~
        path("int number" / IntNumber) { num =>
          get {
            complete(s"other number $num")
          }
        } ~
        path("welcome" / Segment / Segment) { (name, surname) =>
          get {
            complete(s"welcome $name $surname")
          }
        }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(performRequest(HttpMethods.GET, "http://localhost:8080/hello/developer"))

    println(performRequest(HttpMethods.GET, "http://localhost:8080/the_answer_to_life_the_universe_and_everything/42"))

    println(performRequest(HttpMethods.GET, "http://localhost:8080/welcome/Haskell/Curry"))

    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
