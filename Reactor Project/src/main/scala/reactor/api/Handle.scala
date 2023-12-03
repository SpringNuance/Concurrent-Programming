package reactor.api

trait Handle[T] {

  def read(): T

}
