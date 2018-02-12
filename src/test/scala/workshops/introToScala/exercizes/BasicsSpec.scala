package workshops.introToScala.exercizes

import java.util.UUID

import workshops.UnitSpec

class BasicsSpec extends UnitSpec{
  import BasicsSpec._
  "User management system" should {
    "add new user" in {
      val generateId: () => String = () => "aStaticId"
      val ldap = new BetterLdap(List.empty, generateId)

      val obtainedId = ldap.addNewUser("email", "password")

      obtainedId mustBe "aStaticId"
      ldap.users mustBe List(User("aStaticId", "email", "password"))
    }

    "return proper session if user is there" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user))

      ldap.getUserSession("userName", "password") mustBe Session("id")
    }

    "return None if credentials are invalid" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user))

      ldap.getUserSession("email", "password") mustBe None
    }

    "send email with a possibility to reset password if user is in the database" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user), () => "token")

      ldap.askForPasswordResetEmail("email")

      EmailService.getSentEmailAndResetState mustBe Some("email")
      ExercizeSpoiler.getUsersResetPasswordToken(ldap, "email") mustBe Some("token")
    }

    "not send any email if user is not in the database" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user))

      ldap.askForPasswordResetEmail("email2")

      EmailService.getSentEmailAndResetState mustBe None
      ExercizeSpoiler.getUsersResetPasswordToken(ldap, "email") mustBe None
    }

    "reset password if token is valid" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user), () => "token")

      ldap.askForPasswordResetEmail("email")

      ldap.restPassword("token", "newPassword") mustBe "Success!"

      ExercizeSpoiler.getUsersPassword(ldap, "email") mustBe "newPassword"
    }

    "not reset password if token is invalid" in {
      val user = User("id", "email", "password")

      val ldap = new BetterLdap(List(user), () => "token")

      ldap.askForPasswordResetEmail("email")

      ldap.restPassword("invalidToken", "newPassword") mustBe "Failure!"

      ExercizeSpoiler.getUsersPassword(ldap, "email") mustBe "password"
    }


    "do some admin stuff if user is admin" in {
      val user = User("id", "admin@example.com", "password")

      val ldap = new BetterLdap(List(user))

      ldap.doSomeAdminStuff("admin@example.com", "password") mustBe "Admin stuff"
    }

    "reject user if not an admin" in {
      val user = User("id", "admin@example.com", "password")

      val ldap = new BetterLdap(List(user), isAdmin = _ => false)

      ldap.doSomeAdminStuff("admin@example.com", "password") mustBe "Rejected!"
    }

    "reject if user is not found" in {
      val user = User("id", "admin@example.com", "password")

      val ldap = new BetterLdap(List.empty, isAdmin = _ => true)

      ldap.doSomeAdminStuff("admin@example.com", "password") mustBe "User not found!"
    }
  }
}

object BasicsSpec {

  case class User(id: String, email: String, password: String, resetPasswordToken: Option[String] = None)
  case class Session(userId: String)

  class BetterLdap(initalUsers: List[User],
                   superSecureGenerator: () => String = () => UUID.randomUUID().toString,
                   isAdmin: User => Boolean = _.email == "admin@example.com") {
    var users : List[User] = initalUsers


    // - generate new id
    // - create new user object with id, email and password
    // - add new user to users
    def addNewUser(email: String, password: String): String = {
      ???
    }

    // - check if potential user's email and password are equal to expected
    private def validateUser(expectedemail: String, expectedPassword: String)(potentialUser: User): Boolean = ???

    // - find user using validateUser function
    // - create new Session with foundUser id, if user was found
    def getUserSession(email: String, password: String): Option[Session] = ???


    // - find a user with given email
    // - if found:
    // - - generate token using super secure generator
    // - - copy found user and set the reset token
    // - - remove old user from the users list
    // - - add user with token to the users list
    // - - send email using email service (send email singleton method)
    def askForPasswordResetEmail(email: String): Unit = ???

    // - find a user with given token (note the types!)
    // - if found:
    // - - copy found user and set her/his password to a new one
    // - - remove old user from the users list
    // - - add user with new password to users list
    def restPassword(token: String, newPassword: String): String = ???


    // - find user with with given email and password
    // - if user is found check if she/he is an admin
    def doSomeAdminStuff(email: String, password: String): String = ???
 }

  object EmailService{
    private var sentEmail: Option[String] = None
    def sendEmailResetPasswordEmail(email: String): Unit = sentEmail = Some(email)

    // just to make my testing easier
    def getSentEmailAndResetState: Option[String] = {
      val a = sentEmail
      sentEmail = None
      a
    }
  }



}


