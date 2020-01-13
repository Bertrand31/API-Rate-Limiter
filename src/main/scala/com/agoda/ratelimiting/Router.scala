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

  private def handleSuccess(hotels: IO[Array[Hotel]]): IO[Response[IO]] =
    hotels >>= ((arr: Array[Hotel]) => Ok(arr.asJson))

  private def handleLimited = TooManyRequests("Too many requests")

  private def handleReponse(res: Option[IO[Array[Hotel]]]): IO[Response[IO]] =
    res.fold(handleLimited)(handleSuccess)

  private val wrappedGetByCity = RateLimiter.wrap(Bridge.getByCity, 5, 10)
  private val wrappedGetByRoom = RateLimiter.wrap(Bridge.getByRoom, 10, 100)

  def routes[F[_]: Sync]: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city => handleReponse(wrappedGetByCity(city))

      case GET -> Root / "room" / room => handleReponse(wrappedGetByRoom(room))

    }
  }
}
