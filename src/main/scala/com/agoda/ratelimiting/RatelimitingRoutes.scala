package com.agoda.ratelimiting

import cats.effect.{IO, Sync}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Router {

  def routes[F[_]: Sync]: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._
    HttpRoutes.of[IO] {

      case GET -> Root / "test" => Ok("OK")
    }
  }
}
