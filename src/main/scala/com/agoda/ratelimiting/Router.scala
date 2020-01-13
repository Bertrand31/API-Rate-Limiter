package com.agoda.ratelimiting

import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.io._
import io.circe.syntax.EncoderOps
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityEncoder._
import com.agoda.ratelimiting.types.Hotel

object Router {

  object PriceSortingParam extends OptionalQueryParamDecoderMatcher[String]("price-sorting")

  private def handleSuccess: IO[Array[Hotel]] => IO[Response[IO]] =
    _ >>= ((arr: Array[Hotel]) => Ok(arr.asJson))

  private def handleLimited: IO[Response[IO]] = TooManyRequests("Too many requests")

  private def handleReponse: Option[IO[Array[Hotel]]] => IO[Response[IO]] =
    _.fold(handleLimited)(handleSuccess)

  def routes[F[_]: Sync]: HttpRoutes[IO] = {

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city :? PriceSortingParam(sorting) =>
        handleReponse(HotelsController.getByCity(city, sorting))

      case GET -> Root / "room" / room :? PriceSortingParam(sorting) =>
        handleReponse(HotelsController.getByRoom(room, sorting))

    }
  }
}
