package workshops.akkaHttp.part3

// those two are required to be imported
import java.time.{Instant, LocalDateTime, ZoneId}

import spray.json._
import DefaultJsonProtocol._

import scala.collection.immutable.Map
import scala.util.Try


object JsonSupport {
import workshops.Utils._

  def main(args: Array[String]): Unit = {
    printWithHeader("Simple class in 2 options")
    implicit val superClassFormat = jsonFormat2(SuperClass)
    println(SuperClass("one", 2).toJson(superClassFormat).prettyPrint)
    println(SuperClass("one", 2).toJson.compactPrint)

    printWithHeader("class with nested class")
    val otherClassFormat = jsonFormat1(OtherClass)
    println(OtherClass(SuperClass("one", 2)).toJson(otherClassFormat).prettyPrint)

    printWithHeader("class with companion object")
    implicit val classWithCompanionObjectFormat = jsonFormat1(ClassWithCompanionObject.apply)
    println(ClassWithCompanionObject(18L).toJson.prettyPrint)
    printWithHeader("Parsing")
    println("""{"field1": 18}""".parseJson.convertTo[ClassWithCompanionObject])

    printWithHeader("Parsing malformed json")
    println(Try("""{"field1: 18}""".parseJson.convertTo[ClassWithCompanionObject]))

    printWithHeader("Parsing json of other format")
    println(Try("""{"field": 18}""".parseJson.convertTo[ClassWithCompanionObject]))

    printWithHeader("Spray handles optionals quite well")
    implicit val classWithOptionalFormat = jsonFormat1(ClassWithOptional)

    println(ClassWithOptional(Some("Optional set")).toJson.prettyPrint)
    println(ClassWithOptional(None).toJson.prettyPrint)
    printWithHeader("Even with reading")
    println("{}".parseJson.convertTo[ClassWithOptional])

    implicit val customReader = new JsonReader[AnotherClass] {
      override def read(json: JsValue) = {
        json.asJsObject("not an object").getFields("field1", "field2") match{
          case Seq(JsString(field1), JsNumber(field2)) => AnotherClass(field1, field2.toInt)
          case _ => throw new IllegalArgumentException("wrong format")
        }
      }
    }

    printWithHeader("Parsing json of other format")
    // converTo requires implicit cannot pass reader ¯\_(ツ)_/¯
    println(Try("""{"field1": "string", "field2": 2}""".parseJson.convertTo[AnotherClass]))


    implicit val dateTimeFormat = new JsonFormat[LocalDateTime] {
      val zoneId = ZoneId.of("UTC")
      override def write(obj: LocalDateTime) = {
        JsNumber(obj.atZone(zoneId).toInstant.toEpochMilli)
      }

      override def read(json: JsValue) = json match {
        case JsNumber(timestamp) => Instant.ofEpochMilli(timestamp.toLong).atZone(zoneId).toLocalDateTime
        case _ =>  throw new IllegalArgumentException("wrong format")
      }
    }

    printWithHeader("Custom format")
    println(LocalDateTime.now().toJson(dateTimeFormat).prettyPrint)

   implicit val customJsonFormat = new RootJsonFormat[ClassWIthoutApply] {
     override def write(obj: ClassWIthoutApply) = JsObject("createdAt" -> obj.createdAt.toJson)

     override def read(json: JsValue) = json.asJsObject.getFields("createdAt") match {
       case Seq(timestamp) => new ClassWIthoutApply(timestamp.convertTo[LocalDateTime])
       case _ =>  throw new IllegalArgumentException("wrong format")
     }
   }
    printWithHeader("Custom root format")
    val createdAt = LocalDateTime.of(2017, 8, 29, 21, 3)
    println(new ClassWIthoutApply(createdAt).toJson.prettyPrint)
    println("""{"createdAt": 1504040062780}""".parseJson.convertTo[ClassWIthoutApply])
  }

  case class SuperClass(field1: String, field2: Int)
  case class OtherClass(field: SuperClass)

  case class ClassWithOptional(optionalField: Option[String])
  case class ClassWithCompanionObject(field1: Long)
  object ClassWithCompanionObject{
    def totalyNotImportantMethod(arg: String): String = "not anything important"
  }

  case class AnotherClass(field1: String, field2: Int)
  class ClassWIthoutApply(val createdAt: LocalDateTime)
}
