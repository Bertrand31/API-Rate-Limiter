package com.agoda.ratelimiting

import scala.concurrent.duration.DurationInt
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.implicits._
import fs2.Stream
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import org.http4s.server.blaze.BlazeServerBuilder

object SickleServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    val httpApp = (
      Router.routes[IO]
    ).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withIdleTimeout(10.minutes)
      .withHttpApp(httpApp)
      .serve
  }.drain
}

object Main extends IOApp {

  def run(args: List[String]) =
    SickleServer
      .stream[IO]
      .compile
      .drain
      .as(ExitCode.Success)
}
