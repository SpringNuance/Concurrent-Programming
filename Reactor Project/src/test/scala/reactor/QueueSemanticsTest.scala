package reactor

import org.scalatest.concurrent.TimeLimitedTests
import reactor.api.{Event, EventHandler, Handle}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.time.{Seconds, Span}

class QueueSemanticsTest extends AnyFunSuite with TimeLimitedTests {

  //The time limit is arbitrary and dependent on the computer
  override def timeLimit: Span = Span(10, Seconds)

  class IntegerHandle(val i: Integer) extends Handle[Integer] {
    def this() = { this(scala.util.Random.nextInt())}
    override def read(): Integer = scala.util.Random.nextInt()
  }

  class IntegerHandler(h: Handle[Integer]) extends EventHandler[Integer] {
    override def getHandle: Handle[Integer] = h
    override def handleEvent(arg: Integer): Unit = { } //do nothing
  }

  def generateIntegerEvent: Event[Integer] = {
    val h = new IntegerHandle()
    Event(h.read(), new IntegerHandler(h))
  }

  test("the queue is empty when created") {
    val q = new BlockingEventQueue[Integer](10)

    assert(q.getCapacity === 10)
    assert(q.getSize === 0)
  }

  test("should not accept null Event") {
    val q = new BlockingEventQueue[Integer](10)
    
    q.enqueue(null)
    assert(q.getSize === 0)
  }

  test("the queue returns inserted elements") {
    val q = new BlockingEventQueue[Integer](10)

    val e = generateIntegerEvent
    q.enqueue(e)

    assert(q.getSize == 1)
    assert(q.dequeue === e)
  }

  test("the queue retains the order of elements") {
    val q = new BlockingEventQueue[Integer](10)
    val e1 = generateIntegerEvent
    val e2 = generateIntegerEvent
    val e3 = generateIntegerEvent

    q.enqueue(e1)
    q.enqueue(e2)
    q.enqueue(e3)

    assert(q.getSize === 3)
    assert(q.dequeue === e1)
    assert(q.dequeue === e2)
    assert(q.dequeue === e3)
  }

  test("the queue implements getAll") {
    val q = new BlockingEventQueue[Integer](10)
    val e1 = generateIntegerEvent
    val e2 = generateIntegerEvent
    val e3 = generateIntegerEvent

    q.enqueue(e1)
    q.enqueue(e2)
    q.enqueue(e3)
    val everything = q.getAll
    
    assert(q.getSize === 0)
    assert(everything.length === 3)
    assert(everything(0) === e1)
    assert(everything(1) === e2)
    assert(everything(2) === e3)
  }


}
