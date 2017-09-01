package workshops.akkaHttp.part3.exercises

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import workshops.UnitSpec
import spray.json._

class JsonHandlingSpec2 extends UnitSpec {

  import JsonHandlingSpec2._

  "handling UserData" should {
    val json =
      """
        |{
        |  "type": "User",
        |  "attributes": {
        |   "first-name": "Haskell",
        |   "last-name": "Curry",
        |   "address": {
        |     "type": "Address",
        |     "attributes": {
        |       "street": "street",
        |       "city": "city",
        |       "zip-code": "00-000"
        |     }
        |   }
        |  }
        |}
      """.stripMargin.parseJson
    val userData = UserData("Haskell", "Curry", Address("street", "city", "00-000"))
    "serialize it properly" in {
      userData.toJson mustBe json
    }

    "deserialize it properly" in {
      json.convertTo[UserData] mustBe userData
    }
  }

  //solution here

  implicit def userDataFormat: RootJsonFormat[UserData] = ???
}

object JsonHandlingSpec2 {

  case class Address(street: String, city: String, zipCode: String)

  case class UserData(firstName: String, lastName: String, address: Address)

}
