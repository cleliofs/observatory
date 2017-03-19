package observatory

import observatory.Extraction.locateTemperatures
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("given a year and station file, locate temperature") {
    val temperatures = locateTemperatures(year = 2015, stationsFile = "/stations.csv", temperaturesFile = "/2015-2.csv")
    assert(temperatures.size == 10)
  }
  
}