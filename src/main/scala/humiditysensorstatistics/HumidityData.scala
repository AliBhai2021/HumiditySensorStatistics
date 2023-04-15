package humiditysensorstatistics

case class HumidityData(sum: Int, count: Int, min:Int, max:Int) {
  def avg: Int = (count > 0) match {
    case true => sum / count
    case false => 0
  }
}
case class SensorStats(min: Int, avg: Int, max: Int)
