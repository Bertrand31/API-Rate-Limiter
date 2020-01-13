package com.agoda.ratelimiting

import scala.concurrent.duration.DurationInt
import cats.effect.{ConcurrentEffect, ContextShift, Timer, ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.implicits._
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder

object AgodaServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    val httpApp = (
      Router.routes[IO]
    ).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withIdleTimeout(2.minutes)
      .withHttpApp(httpApp)
      .serve
  }.drain
}

object Main extends IOApp {

  def run(args: List[String]) =
    AgodaServer
      .stream[IO]
      .compile
      .drain
      .as(ExitCode.Success)
}
