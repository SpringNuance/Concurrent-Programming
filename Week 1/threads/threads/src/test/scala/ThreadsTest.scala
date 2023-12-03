package threads
import org.scalatest._
import flatspec._

class ThreadsTest extends AnyFlatSpec with BeforeAndAfterAll {
  val arr = Array(1,2,3,4,5,6,7,8,9,10,11)
  def square(x: Int): Int = x*x
  val thread = new Threads
  val bs = thread.parallelMap(square, arr)

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
  }
  
  println(s"arr: ${arr.mkString(",")}")
  println(s"bs: ${bs.mkString(",")}")
  
  
  "The new array" should "have same length as old array" in {
    assert(bs.length == arr.length)
  }
  it should "be the square of the old array" in {
    assert((bs.toSeq).equals(arr.map(x =>x*x).toSeq))
  }
}
