package atomicReference
import instrumentation.TestHelper._
import instrumentation.TestUtils._
import instrumentation._
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite

class SimpleAtomicReferenceTest extends AnyFunSuite with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
  }
  
  test("Should update value to 1") {
    val integer = new SimpleAtomicReference(0)
    val intValue = integer.compareAndSet(0, 1)
    assert(intValue)
    assert(integer.get == 1)
  }

  test("should make sure that only one thread considers writing successful if they race") {
    testManySchedules(2, sched => {
      val sa = new SchedulableAtomicReference(0, sched)
      val ops = List(
        () => sa.compareAndSet(0, 1),
        () => sa.compareAndSet(0, 2))
      ( //for (i <- (1 to 2).toList) yield () => sa.compareAndSet(0, i),
        ops,
        results => {
          val t1 = results(0) // change the counter from 0 to 1
          val t2 = results(1) // change the counter from 0 to 2
          val res = sa.get
          ((t1 == true && t2 == false && res == 1) || (t1 == false && t2 == true && res == 2),
            s"Changed from 0 to 1? $t1 or changed from 0 to 2? $t2. Result is: $res")

        })
    })
  }
  test("should not update the value when the test fails") {
    testSequential[(Boolean, Int)] { sched =>
      val int = new SchedulableAtomicReference(0, sched)
      val test = int.compareAndSet(1, 2)
      val v = int.get
      if (test != false) (false, s"Expected compareAndSet to return false but got $test")
      else if (v != 0) (false, s"CompareAndSet should not modify value if test fails but found $v")
      else (true, "")

    }
  }
}
