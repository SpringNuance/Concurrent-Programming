package monitorBlocking
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import instrumentation.TestHelper._
import instrumentation.TestUtils._
import instrumentation._

class MonitorBlockingQueuesTest extends AnyFunSuite with BeforeAndAfterAll {

  test("After inserting elemenets to capacity, the queue should be full"){
    val q = new MonitorBlockingQueue[Int](2)
    for(i <- 0 until 2) q.put(i)
    assert(q.queue.size == 2)
    assert(!q.queue.isEmpty)
  }
  test("A newly formed queue should be empty"){
    val q = new MonitorBlockingQueue[Int](2)
    assert(q.queue.isEmpty)
    assert(q.queue.size == 0)
  }
  test("The queue should be empty after the sequence of operations"){
    testManySchedules(10, sched =>{
      val q = new SchedulableMonitorBlocking[Int](2,sched)     
    
      val ops = List(
        () => q.take(),
        () => q.take(),
        () => q.put(0),       
        () => q.put(1),
        () => q.take(),
        () => q.take(),
        () => q.put(2),
        () => q.put(3),
        () => q.take(),
        () => q.put(3)
        
      )
      def result(res: List[Any]) = (q.queue.isEmpty, "The queue should be empty : ["+q.queue.mkString(",")+ "]")
      (ops,result)
    })
  }
  test("The queue should contain a value once inserted") {
    testManySchedules(3,sched => {
      val q = new SchedulableMonitorBlocking[Int](3,sched)
      (for (i <- (1 to 3).toList) yield () => q.put(i),
          results => (q.queue.contains(2), "The queue should have 2 as its element"))
    })
  }
}
