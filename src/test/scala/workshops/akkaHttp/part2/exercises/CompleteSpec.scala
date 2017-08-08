package workshops.akkaHttp.part2.exercises

import akka.Done
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import workshops.UnitSpec

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CompleteSpec extends UnitSpec with ScalatestRouteTest {

  import CompleteSpec._

  "Post service" should {
    "return post if its present" in {
      val blogPost = BlogPost("tite", "content")
      val route = new PostsController(new InMemoryPostsService(Vector(blogPost))).route
      Get("/post/0") ~> route ~> check {
        responseAs[String] mustBe serializePost(blogPost)
        status mustBe StatusCodes.OK
      }
    }

    "return 404 if post is not found" in {
      val route = new PostsController(new InMemoryPostsService(Vector())).route
      Get("/post/0") ~> route ~> check {
        status mustBe StatusCodes.NotFound
      }
    }

    "return internal server error in case of database problem" in {
      val route = new PostsController(new FailingPostsService).route
      Get("/post/0") ~> route ~> check {
        status mustBe StatusCodes.InternalServerError
      }
    }

    "return list of 5 records from the 1st page if no parameter set" in {
      val posts = buildBlogPostsList(25)
      val route = new PostsController(new InMemoryPostsService(posts)).route
      Get("/posts") ~> route ~> check {
        status mustBe StatusCodes.OK
        responseAs[String] mustBe serializeList(posts.take(5))
      }
    }

    "return list of 25 records if per page is specified" in {
      val posts = buildBlogPostsList(25)
      val route = new PostsController(new InMemoryPostsService(posts)).route
      Get("/posts?perPage=25") ~> route ~> check {
        responseAs[String] mustBe serializeList(posts)
      }
    }

    "return 10 record from the second page if both per page and page parameters are given" in {
      val service = new InMemoryPostsService(buildBlogPostsList(30))
      val route = new PostsController(service).route
      Get("/posts?perPage=10&page=2") ~> route ~> check {
        responseAs[String] mustBe serializeList(futureResults(service.index(10, 2)))
      }
    }

    "create new blog post" in {
      val service = new InMemoryPostsService(Vector())
      val route = new PostsController(service).route
      Post("/posts?title=title&content=content") ~> route ~> check {
        status mustBe StatusCodes.Created
        service.posts.length mustBe 1
      }
    }

    "return internal server error if there is a database problem" in {
      val service = new FailingPostsService
      val route = new PostsController(service).route
      Post("/posts?title=title&content=content") ~> route ~> check {
        status mustBe StatusCodes.InternalServerError
      }
    }
  }


  def buildBlogPostsList(count: Int) = Vector.tabulate(count)(i => BlogPost(s"title$i", s"content$i"))
}

object CompleteSpec {
  class PostsController(service: PostService) {
    implicit val ec = scala.concurrent.ExecutionContext.global

    def route: Route = ??? // uncomment routes as you proceed with task, you can check what will happen when you have
                          // at least 1 ??? spoiler: it wont work
      // postRoute //~ postsRoute

    private def postRoute: Route = ??? // remeber to use Success and Failure from scala.util not form Akka

    private def postsRoute: Route = ??? //Get and post for /posts
  }

  // do not change anything below this line

  case class BlogPost(title: String, content: String)

  class ElementNotFound extends Exception

  def serializePost(blogPost: BlogPost) = s"${blogPost.title} ${blogPost.content}"

  def serializeList(posts: Vector[BlogPost]) = posts.map(serializePost).mkString("[", ", ", "]")

  trait PostService {
    def find(id: Int)(implicit ec: ExecutionContext): Future[BlogPost]

    def create(title: String, content: String)(implicit ec: ExecutionContext): Future[Done]

    def index(perPage: Int, page: Int)(implicit ec: ExecutionContext): Future[Vector[BlogPost]]
  }

  class FailingPostsService extends PostService {
    override def find(id: Int)(implicit ec: ExecutionContext): Future[BlogPost] = Future.failed(new Exception)

    override def create(title: String, content: String)(implicit ec: ExecutionContext): Future[Done] =
      Future.failed(new Exception)

    override def index(perPage: Int, page: Int)(implicit ec: ExecutionContext): Future[Vector[BlogPost]] =
      Future.failed(new Exception)
  }

  class InMemoryPostsService(initialPosts: Vector[BlogPost]) extends PostService {
    var posts: Vector[BlogPost] = initialPosts

    def find(id: Int)(implicit ec: ExecutionContext): Future[BlogPost] = Future {
      posts.lift(id).getOrElse(throw new ElementNotFound)
    }

    def create(title: String, content: String)(implicit ec: ExecutionContext): Future[Done] = Future {
      posts = posts :+ BlogPost(title, content)
      Done
    }

    def index(perPage: Int, page: Int)(implicit ec: ExecutionContext): Future[Vector[BlogPost]] = Future {
      posts.sliding(perPage).toVector.lift(page).getOrElse(Vector())
    }
  }
}
