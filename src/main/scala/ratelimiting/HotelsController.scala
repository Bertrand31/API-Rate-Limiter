package ratelimiting

import scala.concurrent.duration.DurationInt
import cats.effect.IO
import cats.implicits._
import org.http4s.Response
import org.http4s.dsl.io._
import io.circe.syntax.EncoderOps
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import ratelimiting.types.Hotel

class HotelsController(implicit val bridge: Bridge) {

  private val handleSuccess: IO[Array[Hotel]] => IO[Response[IO]] =
    _ >>= ((arr: Array[Hotel]) => Ok(arr.asJson))

  private val handleLimited: IO[Response[IO]] = TooManyRequests("Too many requests")

  private val handleReponse: Option[IO[Array[Hotel]]] => IO[Response[IO]] =
    _.fold(handleLimited)(handleSuccess)

  private def handleSorting(sorting: Option[String])(hotels: Array[Hotel]): Array[Hotel] =
    sorting.map(_.toLowerCase).fold(hotels)({
      case "asc" => hotels.sortBy(_.price)
      case _ =>     hotels.sortBy(- _.price)
    })

  private val safeGetByCity = RateLimiter.wrapUnary(bridge.getByCity, 5.seconds, 10)
  private val safeGetByRoom = RateLimiter.wrapUnary(bridge.getByRoom, 10.seconds, 100)

  def getByCity(city: String, sorting: Option[String]): IO[Response[IO]] =
    handleReponse(safeGetByCity(city).map(_.map(handleSorting(sorting))))

  def getByRoom(room: String, sorting: Option[String]): IO[Response[IO]] =
    handleReponse(safeGetByRoom(room).map(_.map(handleSorting(sorting))))
}
