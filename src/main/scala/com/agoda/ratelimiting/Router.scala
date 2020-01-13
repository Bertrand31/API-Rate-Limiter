package com.agoda.ratelimiting

import cats.effect.{IO, Sync}
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object Router {

  object PriceSortingParam extends OptionalQueryParamDecoderMatcher[String]("price-sorting")

  def routes[F[_]: Sync]: HttpRoutes[IO] = {

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city :? PriceSortingParam(sorting) =>
        HotelsController.getByCity(city, sorting)

      case GET -> Root / "room" / room :? PriceSortingParam(sorting) =>
        HotelsController.getByRoom(room, sorting)

    }
  }
}
