package workshops.akkaHttp.part3.answers

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
  implicit def addressFormat: RootJsonFormat[Address] =  new RootJsonFormat[Address] {
    override def write(obj: Address): JsValue = JsObject(Map(
      "type" -> JsString("Address"),
      "attributes" -> JsObject(Map(
        "street" -> JsString(obj.street),
        "city" -> JsString(obj.city),
        "zip-code" -> JsString(obj.zipCode)
      ))
    ))

    override def read(json: JsValue): Address = json.asJsObject.fields("attributes").asJsObject.getFields("street", "city", "zip-code") match {
      case Seq(JsString(street), JsString(city), JsString(zipCode)) => Address(street, city, zipCode)
      case _ => throw new IllegalArgumentException("(ﾉಥ益ಥ）ﾉ ┻━┻")
    }
  }

  implicit def userDataFormat: RootJsonFormat[UserData] = new RootJsonFormat[UserData] {
    override def write(obj: UserData): JsValue = JsObject(Map(
      "type" -> JsString("User"),
      "attributes" -> JsObject(Map(
        "first-name" -> JsString(obj.firstName),
        "last-name" -> JsString(obj.lastName),
        "address" -> obj.address.toJson
      ))
    ))

    override def read(json: JsValue): UserData = json.asJsObject.fields("attributes").asJsObject.getFields("first-name", "last-name", "address") match {
      case Seq(JsString(firstName), JsString(lastName), address: JsObject) => UserData(firstName, lastName, address.convertTo[Address])
      case _ => throw new IllegalArgumentException("┬─┬ノ( º _ ºノ)")
    }
  }
}

object JsonHandlingSpec2 {

  case class Address(street: String, city: String, zipCode: String)

  case class UserData(firstName: String, lastName: String, address: Address)

}
