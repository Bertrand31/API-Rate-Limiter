package ratelimiting

import cats.effect.IO

/** The bridge is the abstraction that the rest of the codebase has to go
  * through in order to access our storage medium (be it a file, a database, etc.)
  * Below is an implementation of the trait that reads from a CSV. If tomorrow we want to
  * replace CSVs with a database, we will only need to write a new bridge
  * that implements this trait, and the transition will be seamless.
  */
trait Bridge {

  def getByCity(city: String): IO[Hotels]

  def getByRoom(room: String): IO[Hotels]
}
