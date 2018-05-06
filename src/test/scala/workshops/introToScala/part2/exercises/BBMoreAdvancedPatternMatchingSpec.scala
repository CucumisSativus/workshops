package workshops.introToScala.part2.exercises

import java.util.concurrent.atomic.AtomicInteger

import workshops.UnitSpec

class BBMoreAdvancedPatternMatchingSpec extends UnitSpec{
  "a scala adept" should {
    "be able to create simple apply" in {
      class MyFile(val path: String)
      class MyFileReader(file: MyFile){
        def read: String = s"Reading from ${file.path}"
      }

      object MyFileReader{
        def apply(path: String): MyFileReader = ???
      }

      MyFileReader("myPath").read mustBe s"Reading from myPath"
    }

    "be able to create simple unapply" in {
      class SimpleClass(val arg: String){}

      object SimpleClass{
        def unapply(arg: SimpleClass): Option[String] = ???
      }

      val simpleInstance = new SimpleClass("argument")
      val res = simpleInstance match {
        case SimpleClass(argument) => argument
      }

      res mustBe "argument"
    }

    "be able to nicely hide implementation details using apply and unapply" in {
      class MyAtomicClass(private val handler: AtomicInteger){
        def incrementAndGet: Int = handler.incrementAndGet()
      }

      object MyAtomicClass{
        def apply(initialValue: Int): MyAtomicClass = ???
        def unapply(arg: MyAtomicClass): Option[Int] = ???
      }

      val myInstance = MyAtomicClass(10)
      myInstance.incrementAndGet mustBe 11
      val currentValue = myInstance match {
        case MyAtomicClass(current) => current
      }

      currentValue mustBe 11
    }
    "be able to prepare unapply for more complex class" in {
      class User(val firstName: String, val lastName: String){
        def fullName: String = firstName + " " + lastName
      }

      object User{
        def unapply(arg: User): Option[String] = ???
      }

      val firstUser = new User("firstName", "lastName")
      val secondUser = new User("", "")

      def getUserFullName(user: User): String = user match {
        case User(fullName) => fullName
        case _ => "default"
      }

      getUserFullName(firstUser) mustBe "firstName lastName"
      getUserFullName(secondUser) mustBe "default"
    }

    "be able to create custom extractor" in {
      case class Order(price: Int)
      case class User(orders: List[Order])

      object IsVipCustomer{
        // if sum of prices for all orders is greater than 2000
        def unapply(arg: User): Boolean = ???
      }

      object NormalDiscountCalculated{
        // if user have more than 10 orders, return number of orders / 2
        def unapply(arg: User): Option[Int] = ???

      }

      def getDiscountForGivenUser(user: User): Int = user match {
        case IsVipCustomer() => 15
        case NormalDiscountCalculated(discount) => discount
        case _ => 0
      }

      val vipUser1 = User(List(Order(2001)))
      val vipUser2 = User(List.fill(2001)(Order(1)))
      val userWithDiscount = User(List.fill(11)(Order(1)))
      val userWithoutDiscount = User(List(Order(100)))

      getDiscountForGivenUser(vipUser1) mustBe 15
      getDiscountForGivenUser(vipUser2) mustBe 15
      getDiscountForGivenUser(userWithDiscount) mustBe 5
      getDiscountForGivenUser(userWithoutDiscount) mustBe 0
    }
  }
}
