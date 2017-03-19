package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class Station(stnId: Option[Int], wbanId: Option[Int], latitude: Option[String], longitude: Option[String])

object Station {
  def findStation(td: TemperatureDay) = ???

  def apply(line: String): Station = {
    val p = "([0-9]*),([0-9]*),([[\\+\\-]+\\d\\.]*),([[\\+\\-]+\\d\\.]*)".r
    val p(v1, v2, v3, v4) = line

    def optional(s: String) = if (s == "") None else Some(s)

    Station(optional(v1).map(_.toInt), optional(v2).map(_.toInt), optional(v3), optional(v4))
  }
}

case class TemperatureDay(stnId: Option[Int], wbanId: Option[Int], month: Int, day: Int, fahrenheit: Double)

object TemperatureDay {
  def apply(line: String): TemperatureDay = {
    val p = "([0-9]*),([0-9]*),([\\d]+),([\\d]+),([[\\-]+\\d\\.]+)".r
    val p(v1, v2, v3, v4, v5) = line

    def optional(s: String) = if (s == "") None else Some(s)

    TemperatureDay(stnId = optional(v1).map(_.toInt), wbanId = optional(v2).map(_.toInt),
      month = v3.toInt, day = v4.toInt, fahrenheit = v5.toDouble)
  }
}