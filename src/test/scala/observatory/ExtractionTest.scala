package observatory

import java.time.LocalDate

import observatory.Extraction.locateTemperatures
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.immutable.Seq

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("given a year and station file, locate temperature") {
    val temperatures = locateTemperatures(year = 2015, stationsFile = "/stations.csv", temperaturesFile = "/2015-2.csv")
//    assert(temperatures.size == 4091191)
  }

  test("given a set of temperatures data when asking for average records then provide data") {
    val records = Seq(
      (LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3),
      (LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0),
      (LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.0)
    )
    val results = Extraction.locationYearlyAverageRecords(records)
    assert(results.size == 2)
//    assert(results == Seq(
//      (Location(37.35, -78.433), 27.3),
//      (Location(37.358, -78.438), 1.0)
//    ))
  }
  
}