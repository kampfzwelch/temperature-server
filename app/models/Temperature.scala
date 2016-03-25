package models

case class Temperature(temp: Double, room: String, hour : Int, minute : Int, day : Int, month: Int, year: Int)

object Temperature {
  import play.api.libs.json.Json

  implicit val temperatureFormat = Json.format[Temperature]
}