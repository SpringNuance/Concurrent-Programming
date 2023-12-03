# Task A - Blocking Queue

In this task, you must implement a blocking event queue. The queue is a mutable "first in, first out" (FIFO) container for events. Essentially, the blocking event queue is a bounded buffer, that can safely be accessed by multiple threads concurrently.

The Java Language Specification (JLS) [chapter 17](https://docs.oracle.com/javase/specs/jls/se11/html/jls-17.html) defines how JVM monitors work, including interrupt semantics (section 17.2.).

## API

Your task is to implement the class `BlockingEventQueue`. `BlockingEventQueue` has a single type parameter `A` denoting the upper bound for event types stored in the queue. The queue belongs to the package `reactor`.

> Example: `BlockingEventQueue[Shape]` can store `Event[Shape]`, `Event[Square]` and `Event[Circle]`, if both `Square` and `Circle` are sub-classes of `Shape`.

### `class BlockingEventQueue[A]`

#### Constructor

The queue has a single constructor that takes a single `Int` argument, the capacity of the queue. The capacity must be positive and cannot be changed. Initially the queue is empty, containing no events.

#### `enqueue[B <: A](e: Event[B]): Unit`

Wait until the queue is not full and add the specified event to the tail of the queue. If interrupted by `Thread.interrupt()` (before or during the method invocation), this method must throw an `InterruptedException` or set the thread's interrupt status.

#### `dequeue: Event[A]`

Wait until the queue is non-empty and remove the event from the head of the queue and return it. If interrupted by `Thread.interrupt()` (before or during the method invocation), this method must throw an `InterruptedException` or set the thread's interrupt status.

#### `getAll: Seq[Event[A]]`

Remove the entire current contents of the queue and return it (in the order it would be read by `dequeue`) as a `Seq`. This method is optional and not required by the next tasks. The method can be implemented for bonus points.

#### `getSize: Int`

Return the size of the contents of the queue (how many events can be taken from it without putting more of them in).

#### `getCapacity: Int`

Return the maximum size of the queue (how many events it can contain).

## Additional Requirements

The event queue may not accept `null` input to `enqueue`, but Events that contain a `null` value as their payload (the value of the field `data`) must be accepted.

## Submission

Submit your implementation as the file `BlockingEventQueue.scala` through A+. If your implementation uses multiple classes, include all of them in the same file. All of your code must be in the `reactor` package.

The first line of your submission must be a comment line, with you student number, last name and first name.

> Example: //123456 Teekkari, Teemu

## Grading

| Points |        |
| ------ | ------ |
| 60     | Implemented the API correctly |
| 15     | Implemented the optional `getAll` method |
| 15     | Implementation is notification efficient (see below) |
| 10     | Code comments helpful for understanding the implementation |

### Efficiency

This is an optional performance improvement not required for the next task. An implementation is notification efficient, if thread A notifies waiting thread B only and only if there exists a guarantee that the wait condition for thread B has been met, excluding interactions from other threads not A or B.

> Tip: Begin by writing a simple and correct implementation before considering efficiency.
