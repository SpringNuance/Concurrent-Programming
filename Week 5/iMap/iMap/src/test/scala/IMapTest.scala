package imap
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration
import org.scalatest.flatspec.AsyncFlatSpec

class IMapTest extends AnyFunSuite {
  
  val m = new IMap[Int, String]()

  test("should add new element to an empty map"){
    m.update(1, "first")
    val v = Await.result(m(1), Duration.Inf)
    assert(v == "first")
  }

  test("should result in an exception if update is called again on a key"){
    assertThrows[Exception] {
       m.update(1, "one")
    }
    val v = Await.result(m(1), Duration.Inf)
    assert(v == "first")
  }

  test("should return a future when apply is called with a new key"){
    val fv:Future[String] = m.apply(2)
    m.update(2, "second")
    val v = Await.result(fv, Duration.Inf)
    assert(v == "second")
  }

  test("should return future of the old promise when apply is called on an old key"){
    val fv:Future[String] = m.apply(2)
    val v = Await.result(fv, Duration.Inf)
    assert(v == "second") 
  }

  test("should execute the callback successfully when value is inserted for past apply"){
    val fv:Future[String] = m.apply(3)
    val p = Promise[String]()
    val f = p.future

    m.whenReady(doStaff, p, fv)
    m.update(3, "three")

    val v = Await.result(fv, Duration.Inf)
    assert(v == "three")
    val v2 = Await.result(f, Duration.Inf)
    assert(v2 == v.toUpperCase())
  }

  def doStaff(v:String): String = {
    v.toUpperCase()
  }
}