import org.scalatest.FlatSpec
import scala.concurrent.duration.DurationInt
import ratelimiting.RateLimiter

class RateLimiterSpec extends FlatSpec {

  behavior of "the rate limiter"

  def plusOne: Int => Int = _ + 1

  val wrappedPlusOne = RateLimiter.wrapUnary(plusOne, 1.seconds, 2)

  it should "return None when the rate limit is met, and return Some anew after a cooldown" in {

    assert(wrappedPlusOne(2) == Some(3))
    assert(wrappedPlusOne(2) == Some(3))
    assert(wrappedPlusOne(2) == None)
    Thread.sleep(1000)
    assert(wrappedPlusOne(2) == Some(3))
  }
}
