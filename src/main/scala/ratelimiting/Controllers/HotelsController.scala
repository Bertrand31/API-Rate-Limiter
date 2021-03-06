package ratelimiting

import scala.concurrent.duration.DurationInt
import cats.data.Nested
import cats.effect.IO
import cats.implicits._
import org.http4s.Response
import org.http4s.dsl.io.{Ok, TooManyRequests, http4sOkSyntax, http4sTooManyRequestsSyntax}
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder

class HotelsController(implicit val bridge: Bridge) {

  private val handleSuccess: IO[Hotels] => IO[Response[IO]] = _ >>= (Ok(_))

  private val handleLimited: IO[Response[IO]] = TooManyRequests("Too many requests")

  private val handleReponse: Nested[Option, IO, Hotels] => IO[Response[IO]] =
    _.value.fold(handleLimited)(handleSuccess)

  private def handleSorting(sorting: Option[String])(hotels: Hotels): Hotels =
    sorting.map(_.toLowerCase).fold(hotels)({
      case "asc" => hotels.sortBy(_.price)
      case _     => hotels.sortBy(- _.price)
    })

  private val safeGetByCity = RateLimiter.wrapUnary(bridge.getByCity, 5.seconds, 10)
  private val safeGetByRoom = RateLimiter.wrapUnary(bridge.getByRoom, 10.seconds, 100)

  def getByCity(city: String, sorting: Option[String]): IO[Response[IO]] =
    handleReponse {
      Nested(safeGetByCity(city)).map(handleSorting(sorting))
    }

  def getByRoom(room: String, sorting: Option[String]): IO[Response[IO]] =
    handleReponse {
      Nested(safeGetByRoom(room)).map(handleSorting(sorting))
    }
}
