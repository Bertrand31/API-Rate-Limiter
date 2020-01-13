package com.agoda.ratelimiting

import scala.io.Source
import cats.effect.IO
import com.agoda.ratelimiting.types.Hotel

object Bridge {

  private def arrayToHotel(arr: Array[String]): Hotel = {
    val Array(city, id, room, price) = arr
    Hotel(city, id.toInt, room, price.toInt)
  }

  // We're keeping this a lazy iterator re-created on every access in order to simulate a database
  // more accurately: the data gets loaded every time it is accessed, instead of held in memory.
  private def fakeTable: IO[Array[Hotel]] =
    IO {
      Source.fromFile("src/main/resources/hoteldb.csv")
        .getLines
        .drop(1)
        .map(_.split(","))
        .map(arrayToHotel)
        .toArray
    }

  def getByCity(city: String): IO[Array[Hotel]] =
    fakeTable.map(_.filter(_.city.toLowerCase == city.toLowerCase))

  def getByRoom(room: String): IO[Array[Hotel]] =
    fakeTable.map(_.filter(_.room.toLowerCase == room.toLowerCase))
}
