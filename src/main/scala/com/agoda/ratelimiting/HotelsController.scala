package com.agoda.ratelimiting

import scala.concurrent.duration.DurationInt
import cats.effect.IO
import com.agoda.ratelimiting.types.Hotel

object HotelsController {

  private def handleSorting(sorting: Option[String])(hotels: Array[Hotel]): Array[Hotel] =
    sorting.map(_.toLowerCase).fold(hotels)({
      case "asc" => hotels.sortBy(_.price)
      case _ =>  hotels.sortBy(- _.price)
    })

  private val safeGetByCity = RateLimiter.wrapUnary(CSVBridge.getByCity, 5.seconds, 10)
  private val safeGetByRoom = RateLimiter.wrapUnary(CSVBridge.getByRoom, 10.seconds, 100)

  def getByCity(city: String, sorting: Option[String]): Option[IO[Array[Hotel]]] =
    safeGetByCity(city).map(_.map(handleSorting(sorting)))

  def getByRoom(room: String, sorting: Option[String]): Option[IO[Array[Hotel]]] =
    safeGetByRoom(room).map(_.map(handleSorting(sorting)))
}
