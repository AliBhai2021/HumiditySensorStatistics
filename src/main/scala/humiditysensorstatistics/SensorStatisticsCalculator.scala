package humiditysensorstatistics

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing, Sink, Source}
import akka.util.ByteString
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable

class SensorStatisticsCalculator {
  implicit val system: ActorSystem = ActorSystem("HumiditySensorStatistics")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val sensors = mutable.LinkedHashMap[String, HumidityData]()
  var failedMeasurements = 0
  val inValidSensorData = mutable.HashSet[String]()


  def calculateSensorStatistics(directoryPath:File):Unit={
    println(s"Reading CSV files from directory path : ${directoryPath}")
    val fileSource = Source.fromIterator(() => directoryPath.listFiles().iterator)
    val measurementSource = fileSource.flatMapConcat(f => FileIO.fromPath(f.toPath))
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 512, allowTruncation = true))
      .drop(1)
      .map(_.utf8String)
      .map(line => {
        val fields = line.split(",")
        (fields(0), fields(1))
      })
    val sink = Sink.foreach[(String, String)](data => {
      (data._1, data._2) match {
        case (sensorId, humidity) if humidity.forall(_.isDigit) =>
          sensors.put(sensorId, sensors.getOrElse(sensorId, HumidityData(0, 0, Int.MaxValue, Int.MinValue)) match {
            case HumidityData(sum, count, min, max) => HumidityData(sum + humidity.toInt, count + 1, Math.min(min, humidity.toInt), Math.max(max, humidity.toInt))
          })
        case (sensorId, _) =>
          inValidSensorData.add(sensorId)
          failedMeasurements += 1
      }
    })

    measurementSource.runWith(sink).onComplete(_ => {
      val numFilesProcessed = directoryPath.list.length
      val numMeasurementsProcessed = sensors.values.map(_.count).sum
      val numFailedMeasurements = failedMeasurements

      val sensorStatisticsData = sensors.map {
        case (sensorId, humidityData) =>
          inValidSensorData.remove(sensorId)
          val stats = SensorStats(
            min = humidityData.min,
            avg = humidityData.avg,
            max = humidityData.max
          )
          (sensorId, stats)
      }  //++ inValidSensorData.map(x=>(x,SensorStats(None,None,None)))

      println(s"Num of processed files: $numFilesProcessed")
      println(s"Num of processed measurements: $numMeasurementsProcessed")
      println(s"Num of failed measurements: $numFailedMeasurements")
      println("Sensors with highest avg humidity:")
      println("sensor-id,min,avg,max")
      sensorStatisticsData.toList.sortWith(_._2.avg > _._2.avg).foreach {
        case (sensorId, stats) =>
          println(s"$sensorId,${stats.min},${stats.avg},${stats.max}")
      }
      inValidSensorData.foreach(x => println(s"${x},NaN,NaN,NaN"))

      system.terminate()
    })
  }

}
