package com.agoda.ratelimiting

import cats.effect.{IO, Sync}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.http4s.circe.CirceEntityEncoder._
import com.agoda.ratelimiting.types._

object Router {

  implicit val hotelEncoder: Encoder[Hotel] = deriveEncoder[Hotel]

  def routes[F[_]: Sync]: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._

    def handleReponse(res: Option[Array[Hotel]]) = {
      res.fold(TooManyRequests("Too many requests"))((hotels: Array[Hotel]) => Ok(hotels.asJson))
    }

    val wrappedGetByCity = RateLimiter.wrap(Bridge.getByCity, 5, 10)
    val wrappedGetByRoom = RateLimiter.wrap(Bridge.getByRoom, 10, 100)


    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city =>
        handleReponse(wrappedGetByCity(city))

      case GET -> Root / "room" / room =>
        handleReponse(wrappedGetByRoom(room))
    }
  }
}
