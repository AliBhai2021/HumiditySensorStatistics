import java.io.File

import humiditysensorstatistics.SensorStatisticsCalculator

object HumiditySensorStatistics {

  def main(args: Array[String]): Unit = {
    args match {
      case path if path.isEmpty =>
        println(s":::::::::::::: Please enter the DirectoryPath parameter, it should not be empty")
      case path if new File(path(0)).exists && new File(path(0)).isDirectory =>
        println(s"Looking for CSV files in directory path : ${args(0)}")
        new SensorStatisticsCalculator().calculateSensorStatistics(new File(path(0)))
      case _ => println(s"::::::::::::::Directory doesn't exists: ${args(0)}")
    }
  }

}
