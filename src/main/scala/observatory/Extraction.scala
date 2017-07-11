package observatory

import java.io.InputStream
import java.text.DecimalFormat
import java.time.LocalDate

import scala.collection.immutable.{Map, Seq}
import scala.io.Source.fromInputStream

/**
  * 1st milestone: data extraction
  */
object Extraction {

  var stationMap: Map[String, Station] = Map.empty

  val df = new DecimalFormat("##.#")

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val stations: Seq[Station] = readLines(stationsFile).filterNot(_.trim.isEmpty).map(Station(_)).to[Seq]
    val filteredStations = stations.filterNot(s => s.latitude.isEmpty || s.longitude.isEmpty || s.latitude.contains("+00.000") || s.longitude.contains("+00.000"))

    val temperatureDays = readLines(temperaturesFile).filterNot(_.trim.isEmpty).map(TemperatureDay(_)).to[Seq]

    populateStationMap(filteredStations)

    var i=0

    temperatureDays.flatMap(td => {
      val stationKey = s"${td.stnId.getOrElse("")}-${td.wbanId.getOrElse("")}"
      val station = stationMap.get(stationKey)


      station.map(s => {
        val date = LocalDate.of(year, td.month, td.day)
        val location = Location(s.latitude.map(s => s.toDouble).get, s.longitude.map(s => s.toDouble).get)
        val celsius = toCelsius(td.fahrenheit)

        val res = (date, location, celsius)

        i+=1
        println(s"$i: $res")

        res

      })
    })
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    val res: Seq[(Location, Double)] = records.groupBy(_._2).map(t => (t._1, t._2.map(_._3).sum / t._2.size)).to[Seq]
    res
  }

  private[observatory] def toCelsius(fahrenheit: Double): Double = {
    df.format((fahrenheit - 32) * (5d/9d)).toDouble
  }

  private[observatory] def populateStationMap(stations: Seq[Station]): Unit = {
    if (stationMap.isEmpty) {
      stationMap = Map(stations map { a => s"${a.stnId.getOrElse("")}-${a.wbanId.getOrElse("")}" -> a }: _*)
    }
  }

  private[observatory] def readLines(file: String) = {
    val stream : InputStream = getClass.getResourceAsStream(file)
    fromInputStream(stream).getLines
  }
}
