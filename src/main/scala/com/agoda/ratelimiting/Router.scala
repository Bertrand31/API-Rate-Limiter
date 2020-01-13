package com.agoda.ratelimiting

import scala.concurrent.duration.DurationInt
import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.io._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder._
import com.agoda.ratelimiting.types._

object Router {

  object PriceSortParam extends OptionalQueryParamDecoderMatcher[String]("price-sorting")

  private def handleSorting(sorting: Option[String])(hotels: Array[Hotel]): Array[Hotel] =
    sorting.fold(hotels)({
      case "ASC" => hotels.sortBy(_.price)
      case _ =>  hotels.sortBy(- _.price)
    })

  private def handleSuccess: IO[Array[Hotel]] => IO[Response[IO]] =
    _ >>= ((arr: Array[Hotel]) => Ok(arr.asJson))

  private def handleLimited: IO[Response[IO]] = TooManyRequests("Too many requests")

  private def handleReponse(sorting: Option[String], res: Option[IO[Array[Hotel]]]): IO[Response[IO]] =
    res.fold(handleLimited)(ioHotels => handleSuccess(ioHotels.map(handleSorting(sorting))))

  private val safeGetByCity = RateLimiter.wrapUnary(CSVBridge.getByCity, 5.seconds, 10)
  private val safeGetByRoom = RateLimiter.wrapUnary(CSVBridge.getByRoom, 10.seconds, 100)

  def routes[F[_]: Sync]: HttpRoutes[IO] = {

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city :? PriceSortParam(sorting) =>
        handleReponse(sorting, safeGetByCity(city))

      case GET -> Root / "room" / room :? PriceSortParam(sorting) =>
        handleReponse(sorting, safeGetByRoom(room))

    }
  }
}
