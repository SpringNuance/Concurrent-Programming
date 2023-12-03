package simpleCountdownLatch
import instrumentation.TestHelper._
import instrumentation.TestUtils._
import instrumentation._
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite

class CountDownLatchTest extends AnyFunSuite{
  test("Should be zero after completing counting"){
    testManySchedules(3, sched =>{
      val cd = new SchedulableCountDownLatch(3, sched)
      val oops = for(i <- (0 until 3).toList) yield () => cd.countDown()      
      def result(res: List[Any]) = (cd.count == 0, s"The count should be zero: ${cd.count == 0}")
      (oops, result)
    })
  }
  test("Should wait until count equals zero"){
    var failedWaiting = false
    testManySchedules(4, sched =>{
      val cd = new SchedulableCountDownLatch(3, sched)
      val oops = ((() => {
        try 
        {
          cd.await()
        }
        catch
        {
          case ex: Exception => (failedWaiting = true)
        }
        }) :: (for(i <- (0 until 3).toList) yield () => cd.countDown()))
      def result(res: List[Any]) = (!failedWaiting, s"Thread at await should wait until count equals zero: ${!failedWaiting}")
      (oops, result)
    })
  }
  test("Should not be zero if the number of threads is less than initCount"){
    testManySchedules(3, sched =>{
      val cd = new SchedulableCountDownLatch(4, sched)
      val oops = for(i <- (0 until 3).toList) yield () => cd.countDown()      
      def result(res: List[Any]) = (cd.count != 0, s"The count should not be zero: ${cd.count != 0}")
      (oops, result)
    })
  }
}