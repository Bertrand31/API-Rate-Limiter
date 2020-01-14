import ratelimiting.types.Hotel

package object ratelimiting {

  type Hotels = Array[Hotel]

  private implicit val bridge = CSVBridge
  val hotelsController = new HotelsController
}
