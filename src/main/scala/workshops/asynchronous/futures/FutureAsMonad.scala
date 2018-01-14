package workshops.asynchronous.futures

import workshops.Utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object FutureAsMonad {
  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    printWithHeader("Doing things on future completed")

    val repo = new UserRepoWhichThrowsException(Seq(User("id", "Haskell", "Curry")))
    val getUser = repo.getUserById("id")

    //pay attention to returned type!
    val result: Unit = getUser.onComplete {
      case Success(user) => println(user.firstName)
      case Failure(ex) => println(ex)
    }

    blockUntilFutureReady(getUser)


    printWithHeader("Doing something with result")

    val prepareUserName = (user: User) => s"${user.firstName.toLowerCase}.${user.lastName.toLowerCase}"
    val prepareEmailAddress = (userName: String) => s"$userName@mydomain.com"

    val getUserAndPrepareEmail: Future[String] =
      repo.getUserById("id")
        .map(prepareUserName)
        .map(prepareEmailAddress)
    // show how to handle it with andThen istead of 2 maps

    println(awaitResult(getUserAndPrepareEmail))

    printWithHeader("Connection futures together")
    val getUserAndPrepareEmail2: Future[String] =
      repo.getUserById("id")
        .map(prepareUserName)
        .map(prepareEmailAddress)

    val emailDeliveryStatus = getUserAndPrepareEmail2.flatMap(EmailService.sendWelcomeEmail)

    println(awaitResult(emailDeliveryStatus))

    val prepareCorruptedEmail = (_: User) => "corrupted_email"

    val getUserPrepareCorruptedEmailAndTryToSendEmail: Future[EmailDeliveryStatus] =
      repo.getUserById("id")
        .map(prepareCorruptedEmail)
        .flatMap(EmailService.sendWelcomeEmail)

    println(awaitResult(getUserPrepareCorruptedEmailAndTryToSendEmail))


    printWithHeader("Recovering from failures")
    val getUserPrepareCorruptedEmailAndTryToSendEmailFromLegacy: Future[EmailDeliveryStatus] =
      repo.getUserById("id")
        .map(prepareCorruptedEmail)
        .flatMap(EmailServiceWhichTrhowsException.sendWeclomeEmail)
        .map(_ => EmailDelivered)
        .recover {
          case ex: EmailInfrastructureException => EmailDeliveryFailed(ex.getMessage)
        }

    println(awaitResult(getUserPrepareCorruptedEmailAndTryToSendEmailFromLegacy))


    printWithHeader("Side effects")

    val getUserPrepareCorruptedEmailAndTryToSendEmailFromLegacyAndNotifyEventBus: Future[EmailDeliveryStatus] =
      repo.getUserById("id")
        .andThen {
          case Success(user) => ThreadSafeEventBus.publish(UserEvent(user))
          case Failure(ex) => println("We've got problems with retrieving user!")
        }
        .map(prepareCorruptedEmail)
        .flatMap(EmailServiceWhichTrhowsException.sendWeclomeEmail)
        .map(_ => EmailDelivered)
        .recover {
          case ex: EmailInfrastructureException => EmailDeliveryFailed(ex.getMessage)
        }
        .andThen {
          case Success(devliveryStaatus) => ThreadSafeEventBus.publish(EmailEvent(devliveryStaatus))
          case Failure(ex) => println("dont know how but we got here!")
        }

    println(awaitResult(getUserPrepareCorruptedEmailAndTryToSendEmailFromLegacyAndNotifyEventBus))


    printWithHeader("Making all these futures readable")


    val welcomeService = new WelcomeService(repo)

    println(awaitResult(welcomeService.getUserAndSendWelcomeEmail("id")))
  }


  case class User(id: String, firstName: String, lastName: String)

  class UserRepoWhichThrowsException(users: Seq[User]) {
    def getUserById(id: String)(implicit ec: ExecutionContext): Future[User] = Future {
      users.find(user => user.id == id).get //just for educational purposes, do not use Option.get in production code
    }
  }

  sealed trait EmailDeliveryStatus

  case object EmailDelivered extends EmailDeliveryStatus

  case class EmailDeliveryFailed(reason: String) extends EmailDeliveryStatus

  object EmailService {
    def sendWelcomeEmail(to: String)(implicit ec: ExecutionContext): Future[EmailDeliveryStatus] = Future {
      if (to.contains("@")) EmailDelivered
      else EmailDeliveryFailed("wrong email address")
    }
  }

  class EmailInfrastructureException extends Exception("email infastructure is down")

  object EmailServiceWhichTrhowsException {
    def sendWeclomeEmail(to: String)(implicit ec: ExecutionContext): Future[Unit] = Future {
      Thread.sleep(3000)
      throw new EmailInfrastructureException
    }
  }

  sealed trait Event

  case class EmailEvent(data: EmailDeliveryStatus) extends Event

  case class UserEvent(data: User) extends Event

  object ThreadSafeEventBus {
    def publish(event: Event): Unit = synchronized {
      println(s"Event bus received event $event")
    }
  }

  class WelcomeService(repo: UserRepoWhichThrowsException){
    def getUserAndSendWelcomeEmail(userId: String)(implicit ec: ExecutionContext): Future[EmailDeliveryStatus] = {
      for{
        user <- getUserById(userId)
        emailAddress = prepareCorruptedEmail(user)
        deliveryStatus <- sendEmailFromLegacy(emailAddress)
      } yield deliveryStatus
    }


    private def getUserById(userId: String)(implicit ec: ExecutionContext): Future[User] = {
      repo.getUserById(userId).andThen {
        case Success(user) => ThreadSafeEventBus.publish(UserEvent(user))
        case Failure(ex) => println("We've got problems with retrieving user!")
      }
    }

    private def sendEmailFromLegacy(to: String)(implicit ec: ExecutionContext): Future[EmailDeliveryStatus] = {
      EmailServiceWhichTrhowsException.sendWeclomeEmail(to)
        .map(_ => EmailDelivered)
        .recover {
          case ex: EmailInfrastructureException => EmailDeliveryFailed(ex.getMessage)
        }.andThen {
        case Success(devliveryStaatus) => ThreadSafeEventBus.publish(EmailEvent(devliveryStaatus))
        case Failure(ex) => println("dont know how but we got here!")
      }
    }

    private val prepareCorruptedEmail = (_: User) => "corrupted_email"
  }
}
