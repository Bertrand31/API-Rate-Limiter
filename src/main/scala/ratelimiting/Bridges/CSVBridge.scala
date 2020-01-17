package ratelimiting

import scala.io.Source
import cats.effect.IO
import ratelimiting.types.Hotel

object CSVBridge extends Bridge {

  private def arrayToHotel(arr: Array[String]): Hotel = {
    val Array(city, id, room, price) = arr
    Hotel(id.toInt, city, room, price.toInt)
  }

  private def fakeTable: IO[Hotels] =
    IO {
      Source.fromFile("src/main/resources/hoteldb.csv")
        .getLines
        .drop(1)
        .map(_.split(","))
        .map(arrayToHotel)
        .toArray
    }

  def getByCity(city: String): IO[Hotels] =
    fakeTable.map(_.filter(_.city.toLowerCase == city.toLowerCase))

  def getByRoom(room: String): IO[Hotels] =
    fakeTable.map(_.filter(_.room.toLowerCase == room.toLowerCase))
}
