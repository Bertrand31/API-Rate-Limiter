package com.agoda.ratelimiting

import scala.collection.mutable.Queue

object RateLimiter {

  def wrap[A, B](fn: A => B, seconds: Int, maxCalls: Int): A => Option[B] = {
    val visits = Queue[Long]()
    (arg: A) => {
      val now = System.currentTimeMillis
      visits.dequeueAll(_ < (now - (seconds * 1000)))
      if (visits.length >= maxCalls) None
      else {
        visits += now
        Some(fn(arg))
      }
    }
  }
}
