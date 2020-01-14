package ratelimiting

import cats.effect.{IO, Sync}
import org.http4s.HttpRoutes
import org.http4s.dsl.io.{GET, OptionalQueryParamDecoderMatcher, Root, /, ->, :?}

object Router {

  object PriceSortingParam extends OptionalQueryParamDecoderMatcher[String]("price-sorting")

  def routes[F[_]: Sync]: HttpRoutes[IO] = {

    HttpRoutes.of[IO] {

      case GET -> Root / "city" / city :? PriceSortingParam(sorting) =>
        hotelsController.getByCity(city, sorting)

      case GET -> Root / "room" / room :? PriceSortingParam(sorting) =>
        hotelsController.getByRoom(room, sorting)

    }
  }
}
