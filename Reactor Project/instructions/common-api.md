# Common API 


These definitions are included in the project template. All of the definitions belong to the package `reactor.api`.

## `case class Event`

Events are made up of an element of data and a handler for processing that element. Pairing these two allows for an event processing system, such as a Reactor, to control when elements of data are processed and by which thread.

### Full Source
```scala
case class Event[T](private val data: T, private val handler: EventHandler[T]) {
  require(handler != null)

  def getEvent: T = { data }

  def getHandler: EventHandler[T] = { handler }

  def handle(): Unit = { handler.handleEvent(data) }

}
```

## `trait Handle[T]`

Represents one end of a (possibly bidirectional) communications channel. Corresponds to e.g. a file handle or a network socket.

### `def read(): T`
Handles have a single method: `read()`. Calls wait for a message (which may be any reference including `null`) to be received from the channel and return it. These received messages are the events that are dispatched by the Reactor. `Handle` implementations may also return objects representing error conditions where applicable; all errors (including `null`) must be returned by the Handle as objects and passed by the Reactor to the application unchanged. Further, a `null` message indicates that the Handle has been closed or has a fatal error and may no longer be `read()`.

You can assume that `read()` will always eventually return a value or respect interrupts by throwing an `InterruptedException`.

Handles are not guaranteed to be thread safe. Each individual handle should be read by only a single thread.

### Full Source
```scala
trait Handle[T] {

  def read(): T

}
```

## `trait EventHandler[T]`

Handles events of type `T` from its `Handle`. 

### `def getHandle(): Handle[T]`

Get the `Handle` from which the `EventHandler` receives events. For any given `EventHandler`, the same `Handle` must be returned every time; in other words, an `EventHandler` may only listen to one Handle).

### `def handleEvent(evt: T): Unit`

Handle an incoming message/event represented by an object `evt` of type `T`, as received from the `Handle`. Messages are dispatched by calling this method.

### Full Source
```scala
trait EventHandler[T] {

  def getHandle: Handle[T]

  def handleEvent(evt: T): Unit

}
```
