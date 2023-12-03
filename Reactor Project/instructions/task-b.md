# Task B - Reactor Pattern

In this task, you must implement a simplified Reactor pattern as described in this section. 

Handles are sources of event data, which the application using a Reactor accepts. It is unkown when, or even if, a Handle will produce an element of data. Hence, it is necessary to read all of the application handles concurrently.

Each handle has exactly one event handler, to which events received by the handle are dispatched (passed) when the handle is registered to a Dispatcher through its corresponding event handler. Events are passed to event handlers through method calls made when `Dispatcher.handleEvents()` is called. Events are not divided into different types (e.g. accept, input, output, close); a registered event handler must receive every message received by its handle (in the order they are received by the handle; there is no ordering between messages received on different handles) while it is registered to a Dispatcher. The Dispatcher makes use of an event queue as implemented in Task A.

## API

Your task is to write Dispatcher part of the Reactor pattern. The `Dispatcher` implementation belongs to the package `reactor` and uses the definitions for `Event`, `Handle` and `EventHandler` from the package `reactor.api`.

### `class Dispatcher`

Implements the Dispatcher part of the Reactor pattern. All method calls to a Dispatcher must be made from the same thread.

#### Constructor

Create a new Dispatcher with no registered Handlers (different Dispatchers are completely separate entities). The constructor takes a single `Int` argument, the capacity of it's internal event queue. This capacity must be positive.

#### `def handleEvents(): Unit`

Repeatedly waits until an event is received (an object returned by a `Handle.read()` on a registered handle) using `select()` and dispatches it by calling `handleEvent()` on the handler whose handle received the event. If interrupted by `Thread.interrupt()` (before or during the method invocation), this method must throw an `InterruptedException` or set the thread's interrupt status. This method must wait until a message is received (on any registered handle). As soon as a new event is available, the event must be dispatched to the corresponding registered `EventHandler`. Polling or busy-waiting for events is not allowed. Events must not be read by the Reactor from a handle before registration (although the application using it may do so). `handleEvents()` returns only when there are no registered handlers.

Events from a specific handle must be handled in the same order they were read from the handle's `read()` method.

#### `def select[_]: Event[_]`

Wait until an event is received (an object returned by a `Handle.read()` on a registered handle). If interrupted by `Thread.interrupt()` (before or during the method invocation), this method may throw an `InterruptedException`. This method must wait until a message is received (on any registered handle). Polling or busy-waiting for events is not allowed. Events must not be read by the Reactor from a handle before registration (although the application using it may do so).

#### `def addHandler[T](h: EventHandler[T]): Unit`

Register an unregistered handler; i.e. start dispatching incoming events for `h`. All events received on the corresponding handle (i.e. `h.getHandle()`) must be (eventually) dispatched to `h`, until `removeHandler(h)` is called or a `null` message is received. Each handler may be registered and deregistered only once. Registered handlers may prevent the program from terminating when the main method returns.

#### `def removeHandler[T](h: EventHandler[T]): Unit`

Deregister a registered handler; i.e. stop dispatching incoming events for `h`. After `removeHandler(h)` is called, no further events may be dispatched to `h`.

## Additional Requirements

Make sure your Reactor implementation does not busy wait or poll.

Any threads your Reactor implementation creates must terminate cleanly. Using unsafe deprecated methods, such as `java.lang.Thread.destroy()`, to terminate threads is not acceptable. Using `System.exit()` or comparable methods that terminate the entire JVM is not an acceptable solution for cleaning up threads.

Note that the `Handle` and `EventHandler` traits are intended to be implemented by the application that uses the Reactor pattern. In other words, your Reactor implementation should not contain any implementations of these interfaces; instead it must behave as specified above when any `Handle` and `EventHandler` implementations compliant with the above specification (such as those in your Hangman implementation in Task C) are used with your code. In particular, this means that your Reactor implementation may not require any additional `Handle` or `EventHandler` methods.

Closing `Handle`s, cleaning up the resources associated with them and terminating pending `Handle.read()` operations is the responsibility of the application using the Reactor. In practice, the responsibility of the `Handle` itself, which will need to have a close method or similar for the application to call.

The Reactor implementation must be totally generic. Do not expose internal state of the Dispatcher beyond the methods defined in this specifications.

It is the responsibility of the application using the reactor to remove handlers from the reactor, when they are no longer viable or the application determines that no more events from the assoaciated handle should be dispatched.

> Tip: The application using the Reactor is single-threaded. Consider what this means for the need to synchronize access to the methods of the Dispatcher.

Do not write any Task C related code into the Reactor.

## Error Handling

The Reactor implementation defined in this assignment does not specify the mechanism used to inform `EventHandler`s of errors in a `Handle`, beyond returning a `null` value.

## Submission

Submit your implementation as the file `Dispatcher.scala` through A+. If your implementation uses multiple classes, include all of them in the same file. All of your code must be in the `reactor` package.

The first line of your submission must be a comment line, with you student number, last name and first name.

> Example: //123456 Teekkari, Teemu

## Grading

| Points |                                                            |
| ------ | ---------------------------------------------------------- |
| 90     | Implemented the API correctly                              |
| 10     | Code comments helpful for understanding the implementation |
