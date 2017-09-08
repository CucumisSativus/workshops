package workshops.akkaHttp.part3.exercises

import java.time.LocalDateTime

import spray.json.DefaultJsonProtocol.jsonFormat2
import workshops.UnitSpec
import spray.json._
import spray.json.DefaultJsonProtocol._
class JsonHandlingSpec1 extends UnitSpec {
  import JsonHandlingSpec1._
  "A json handler" when {
    "handling simple class" should {
      "serialize it properly" in {
        val simpleClass = SimpleClass("field1", 2)
        val expectedJson = JsObject(Map("field1" -> JsString("field1"), "field" -> JsNumber(2)))

        simpleClass.toJson mustBe expectedJson
      }

      "deserialize it properly" in {
        val expectedSimpleClass = SimpleClass("field1", 2)
        val json = JsObject(Map("field1" -> JsString("field1"), "field" -> JsNumber(2)))

        json.convertTo[SimpleClass] mustBe expectedSimpleClass
      }
    }
  }

  // solution here

  implicit def simpleClassFormat: RootJsonFormat[SimpleClass] = ???
}
object JsonHandlingSpec1 {
  import DefaultJsonProtocol._
  case class SimpleClass(field1: String, field: Int)
}
