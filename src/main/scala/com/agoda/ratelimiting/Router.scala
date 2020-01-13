package com.agoda.ratelimiting

import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.http4s.circe.CirceEntityEncoder._
import com.agoda.ratelimiting.types._

object Router {

  implicit val hotelEncoder: Encoder[Hotel] = deriveEncoder[Hotel]

  private def handleSuccess: IO[Array[Hotel]] => IO[Response[IO]] =
    _ >>= ((arr: Array[Hotel]) => Ok(arr.asJson))

  private def handleLimited: IO[Response[IO]] = TooManyRequests("Too many requests")

  private def handleReponse: Option[IO[Array[Hotel]]] => IO[Response[IO]] =
    _.fold(handleLimited)(handleSuccess)

  private val safeGetByCity = RateLimiter.wrap(CSVBridge.getByCity, 5, 10)
  private val safeGetByRoom = RateLimiter.wrap(CSVBridge.getByRoom, 10, 100)

  def routes[F[_]: Sync]: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city => handleReponse(safeGetByCity(city))

      case GET -> Root / "room" / room => handleReponse(safeGetByRoom(room))

    }
  }
}
