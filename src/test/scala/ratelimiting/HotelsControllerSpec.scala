import scala.util.{Try, Success}
import org.scalatest.FlatSpec
import cats.effect.IO
import ratelimiting.Bridge
import ratelimiting.HotelsController
import ratelimiting.types.Hotel

class HotelsControllerSpec extends FlatSpec {

  behavior of "the Hotels controller"

  object FakeBridge extends Bridge {

    def getByCity(city: String): IO[Array[Hotel]] =
      IO {
        Array(
          Hotel(id=1, city=city, "Deluxe", price=10)
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

  val res = hotelsController.getByCity("Paris", None)
}
