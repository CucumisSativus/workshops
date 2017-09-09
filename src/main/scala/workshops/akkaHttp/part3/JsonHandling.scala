package workshops.akkaHttp.part3

import java.time.{Instant, LocalDateTime, ZoneId}

// those two are required to be imported
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.Try


object JsonHandling {

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
        json.asJsObject("not an object").getFields("field1", "field2") match {
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
        case _ => throw new IllegalArgumentException("wrong format")
      }
    }

    printWithHeader("Custom format")
    println(LocalDateTime.now().toJson(dateTimeFormat).prettyPrint)

    implicit val customJsonFormat = new RootJsonFormat[ClassWithoutApply] {
      override def write(obj: ClassWithoutApply) = JsObject("createdAt" -> obj.createdAt.toJson)

      override def read(json: JsValue) = json.asJsObject.getFields("createdAt") match {
        case Seq(timestamp) => new ClassWithoutApply(timestamp.convertTo[LocalDateTime])
        case _ => throw new IllegalArgumentException("wrong format")
      }
    }
    printWithHeader("Custom root format")
    val createdAt = LocalDateTime.of(2017, 8, 29, 21, 3)
    println(new ClassWithoutApply(createdAt).toJson.prettyPrint)
    println("""{"createdAt": 1504040062780}""".parseJson.convertTo[ClassWithoutApply])

    implicit val classToArrayFormat = new RootJsonFormat[ClassWhichWillBeSerializedToArray] {
      override def write(obj: ClassWhichWillBeSerializedToArray) = JsArray(JsString(obj.field1), JsNumber(obj.field2), JsString(obj.field3))

      override def read(json: JsValue) = json match {
        case JsArray(Vector(JsString(field1), JsNumber(field2), JsString(field3))) =>
          ClassWhichWillBeSerializedToArray(field1, field2.toInt, field3)
        case _ => throw new IllegalArgumentException("wrong format")
      }
    }

    printWithHeader("Serialization to array")
    println(ClassWhichWillBeSerializedToArray("1", 2, "3").toJson.prettyPrint)
    println(ClassWhichWillBeSerializedToArray("1", 2, "3").toJson.convertTo[ClassWhichWillBeSerializedToArray])

  }


  case class SuperClass(field1: String, field2: Int)

  case class OtherClass(field: SuperClass)

  case class ClassWithOptional(optionalField: Option[String])

  case class ClassWithCompanionObject(field1: Long)

  object ClassWithCompanionObject {
    def totalyNotImportantMethod(arg: String): String = "not anything important"
  }

  case class AnotherClass(field1: String, field2: Int)

  class ClassWithoutApply(val createdAt: LocalDateTime)

  case class ClassWhichWillBeSerializedToArray(field1: String, field2: Int, field3: String)

}
