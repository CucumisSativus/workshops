package workshops.akkaHttp.part1

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object Routing {
  import workshops.Utils._
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher


    // difference between path, pathPrefix and pathSuffix
    val route =
      pathPrefix("hello") {
        pathSuffix("world") {
          get {
            complete("hello world")
          }
        } ~
          pathSuffix("no content") {
            (get | post) {
              complete(HttpResponse(StatusCodes.NoContent))
            }
          } ~
          pathEnd {
            get {
              complete("hello get")
            } ~
              post {
                complete("hello post")
              }
          }
      }~ // what happens if we forget about ~
        path("logic"){
          get {
            val sum = 1 + 2
            complete(s"$sum") // marshalling
          }
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    printWithHeader("Server online at http://localhost:8080/")

    printWithHeader("performing get request")
    println(performRequest(HttpMethods.GET, "http://localhost:8080/hello"))

    printWithHeader("performing post request")
    println(performRequest(HttpMethods.POST, "http://localhost:8080/hello"))

    printWithHeader("performing request for not exising path")
    println(performRequest(HttpMethods.GET, "http://localhost:8080/wrongPath"))

    printWithHeader("performing request for hello/world path")
    println(performRequest(HttpMethods.GET, "http://localhost:8080/hello/world"))


    printWithHeader("performing request for /logic path")
    println(performRequest(HttpMethods.GET, "http://localhost:8080/logic"))


    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
