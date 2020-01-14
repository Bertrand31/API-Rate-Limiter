package object ratelimiting {

  private implicit val bridge = CSVBridge
  val hotelsController = new HotelsController
}
