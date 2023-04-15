import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import humiditysensorstatistics.SensorStatisticsCalculator
import org.scalatest.{FlatSpec, Matchers, FunSuite}
import org.scalatest.FunSuite

class SensorStatisticsCalculatorSpec extends FunSuite {
  implicit val system = ActorSystem("test")
  implicit val materializer = ActorMaterializer()

  val calculator = new SensorStatisticsCalculator

  test("count an average of valid readings using valid directory") {
    assert(calculator.calculateSensorStatistics(new File("sampleData"))==Unit)
  }

  test("count an average of valid readings using invalid directory") {
    assertThrows[Exception](calculator.calculateSensorStatistics(new File("sampleDataInvalidDirectory")))
  }

  test("count an average of valid readings with directory name as empty") {
    assertThrows[Exception](calculator.calculateSensorStatistics(new File("")))
  }


}
