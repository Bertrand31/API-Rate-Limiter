package com.agoda.ratelimiting

import scala.io.Source
import cats.effect.IO
import com.agoda.ratelimiting.types.Hotel

trait Bridge {

  def getByCity(city: String): IO[Array[Hotel]]

  def getByRoom(room: String): IO[Array[Hotel]]
}

object CSVBridge extends Bridge {

  private def arrayToHotel(arr: Array[String]): Hotel = {
    val Array(city, id, room, price) = arr
    Hotel(city, id.toInt, room, price.toInt)
  }

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
