import scala.util.{Try, Success}
import org.scalatest.FlatSpec
import org.scalatest.compatible.Assertion
import cats.effect.IO
import org.http4s.Response
import org.http4s.dsl.io._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import ratelimiting.{Bridge, HotelsController}
import ratelimiting.types.Hotel

class HotelsControllerSpec extends FlatSpec {

  behavior of "the Hotels controller"

  def compareBodies(a: IO[Response[IO]], b: IO[Response[IO]]): Assertion =
    assert(a.unsafeRunSync.body == b.unsafeRunSync.body)

  object FakeBridge extends Bridge {

    def getByCity(city: String): IO[Array[Hotel]] =
      IO {
        Array(
          Hotel(id=1, city=city, "Deluxe", price=10),
          Hotel(id=1, city=city, "Deluxe", price=5),
          Hotel(id=1, city=city, "Deluxe", price=30)
        )
      }

    def getByRoom(room: String): IO[Array[Hotel]] =
      IO {
        Array(
          Hotel(id=2, city="Bangkok", room=room, price=20)
        )
      }
  }

  implicit val bridge = FakeBridge
  val hotelsController = new HotelsController

  behavior of "the city endpoint controller"

  it should "return unsorted cities when not given a sorting parameter" in {

    val cities = hotelsController.getByCity("Toulouse", None)
    val expected = Ok(
      Array(
        Hotel(id=1, city="Toulouse", "Deluxe", price=10),
        Hotel(id=1, city="Toulouse", "Deluxe", price=5),
        Hotel(id=1, city="Toulouse", "Deluxe", price=30)
      ).asJson
    )
  }

  it should "start denying requests from the 11th onwards when bombing the endpoint" in {

    val attempts =
      (1 to 13)
        .map(_ => hotelsController.getByCity("Paris", None))
        .drop(10)
        .foreach(compareBodies(_, TooManyRequests("Too many requests")))
  }
}
