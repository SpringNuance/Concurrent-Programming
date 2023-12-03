package spinlock

import instrumentation._
import TestHelper._
import TestUtils._
import instrumentation.monitors._
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import Assertions._

class SpinlockTest extends AnyFunSuite {
  

  test("Should return the name of the current thread") {
    val spin = new SpinlockMain1
    val string = spin.get()
    val thread = Thread.currentThread().getName
    assert(string == Thread.currentThread().getName)

  }
  test("should lead to deadlock") {    
    val thrown = intercept[java.lang.AssertionError]{
      testManySchedules(1, sched =>{
       val spin = new SchedulableSpinlock(sched)
       val oops = List( () => spin.get())
       def result(res: List[Any]) = (false, "Should not reach here.")
       (oops, result)
      })
    }
    assert(thrown.getMessage().contains("A possible deadlock!"))
    
  }

}