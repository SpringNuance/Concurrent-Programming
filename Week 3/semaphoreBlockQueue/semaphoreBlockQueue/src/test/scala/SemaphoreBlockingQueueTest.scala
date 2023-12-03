package semaphoreBlockQueue
import instrumentation.TestHelper._
import semaphore._

import instrumentation.TestUtils._
import instrumentation._
import monitors._
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.Assertions._

class SemaphoreBlockingQueueTest extends AnyFunSuite with Matchers{
  val semQueue = new SemaphoreBlockingQueue[Int](2)

  test("A newly formed queue should be empty") {
    val sem = new SemaphoreBlockingQueue[Int](3)
    assert(sem.isEmpty() == true)
    assert(sem.isFull() == false)

  }
  test("After inserting elements to capacity, the queue should be full") {
    val sem = new SemaphoreBlockingQueue[Int](3)
    for (i <- 0 until 3) sem.put(i)
    assert(sem.isEmpty() == false)
    assert(sem.isFull() == true)

  }
  test("The queue should be empty after adding and removing two elements") {
    testManySchedules(6, sched => {
      val q = new SchedulableSemaphoreBlocking[Int](4, sched)
      q.elementSem = new SchedulableSemaphore(0, sched)
      q.capacitySem = new SchedulableSemaphore(4, sched)
      val ops = List(
        () => q.put(1),
        () => q.put(2),
        () => q.take(),
        () => q.put(0),
        () => q.take(),
        () => q.take())
      def results(res:List[Any]) = (q.queue.isEmpty, "The queue should be empty")
      (ops,results)
    })
  }
  test("The queue should uphold the FIFO rule") {
    testManySchedules(2, sched => {
      val q = new SchedulableSemaphoreBlocking[Int](4, sched)
      q.elementSem = new SchedulableSemaphore(0, sched)
      q.capacitySem = new SchedulableSemaphore(4, sched)
      
      val ops = List(
        () => q.put(1),
        () => q.put(0)
               
      )
      (ops, results => {
        val t = results(0)
       // Console.err.println(s"take: $t")
        (q.queue.contains(1), s"The first element from the queue should be $t")
      })
    })
  }
  }