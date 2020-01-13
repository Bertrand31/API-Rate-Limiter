package com.agoda.ratelimiting

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    RatelimitingServer.stream[IO].compile.drain.as(ExitCode.Success)
}