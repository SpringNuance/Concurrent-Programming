package reactor.api

case class Event[T](private val data: T, private val handler: EventHandler[T]) {
  require(handler != null)

  def getEvent: T = { data }

  def getHandler: EventHandler[T] = { handler }

  def handle(): Unit = { handler.handleEvent(data) }

}
