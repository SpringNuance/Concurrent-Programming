package reactor.api

trait EventHandler[T] {

  def getHandle: Handle[T]

  def handleEvent(evt: T): Unit

}
