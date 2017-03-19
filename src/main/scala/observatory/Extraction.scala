package observatory

import java.io.InputStream
import java.text.DecimalFormat
import java.time.LocalDate

import scala.collection.immutable.{Map, Seq}

/**
  * 1st milestone: data extraction
  */
object Extraction {

  var stationMapByStnId: Map[Int, Station] = Map.empty
  var stationMapByWbanId: Map[Int, Station] = Map.empty

  val df = new DecimalFormat("##.#")

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val stations: Seq[Station] = readLines(stationsFile).filterNot(_.trim.isEmpty).map(Station(_)).to[Seq]
    val filteredStations = stations.filterNot(s => s.latitude.isEmpty || s.longitude.isEmpty)

    val temperatureDays = readLines(temperaturesFile).filterNot(_.trim.isEmpty).map(TemperatureDay(_)).to[Seq]

    populateStationMapByStnIdMap(filteredStations)
    populateStationMapByWbanId(filteredStations)

    temperatureDays.flatMap(td => {
      val station = td.stnId.flatMap(id => stationMapByStnId.get(id))
        .orElse(td.wbanId.flatMap(id => stationMapByWbanId.get(id)))

      station.map(s => {
        val date = LocalDate.of(year, td.month, td.day)
        val location = Location(s.latitude.map(s => s.toDouble).get, s.longitude.map(s => s.toDouble).get)
        val celsius = toCelsius(td.fahrenheit)

        (date, location, celsius)
      })
    })
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    ???
  }

  private[observatory] def toCelsius(fahrenheit: Double): Double = {
    df.format((fahrenheit - 32) * (5d/9d)).toDouble
  }

  private[observatory] def populateStationMapByStnIdMap(stations: Seq[Station]): Unit = {
    if (stationMapByStnId.isEmpty) {
      stationMapByStnId = Map(stations.filterNot(_.stnId.isEmpty) map { a => a.stnId.get -> a }: _*)
    }
  }

  private[observatory] def populateStationMapByWbanId(stations: Seq[Station]): Unit = {
    if (stationMapByWbanId.isEmpty) {
      stationMapByWbanId = Map(stations.filterNot(_.wbanId.isEmpty) map { a => a.wbanId.get -> a }: _*)
    }
  }

  private[observatory] def readLines(file: String) = {
    val stream : InputStream = getClass.getResourceAsStream(file)
    scala.io.Source.fromInputStream(stream).getLines
  }
}
