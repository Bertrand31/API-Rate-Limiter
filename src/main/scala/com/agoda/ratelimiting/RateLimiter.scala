package com.agoda.ratelimiting

import scala.collection.mutable.Queue
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration._

object RateLimiter {

  /** Wraps a unary function and changes its signature so that it returns None when the rate limit
    * has been met (thus not running the function), and Some(the output of the function) otherwise.
    */
  def wrapUnary[A, B](fn: A => B, cooldown: Duration = 10.seconds, maxCalls: Int = 50): A => Option[B] = {
    val visits = Queue[Long]()
    (arg: A) => {
      val now = System.currentTimeMillis
      visits.dequeueAll(_ < (now - cooldown.toMillis))
      if (visits.length >= maxCalls) None
      else {
        visits += now
        Some(fn(arg))
      }
    }
  }
}
