package lockFree
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import instrumentation.TestHelper._
import instrumentation.TestUtils._
import instrumentation._

class LockFreeStackTest extends AnyFunSuite{
  
  def contains(e: Int): Boolean = {
   val stack = new LockFreeStack[Int](3)
   var i = 0
   while(stack.top.get.value != e && i < 3){
     stack.pop()
     i += 1
     false
   }
   if(i ==3) false
   true
  }
  test("An inserted node should be found from the tree"){
    testManySchedules(3, sched => {
      val lf = new SchedulableLockFreeStack[Int](3,sched)
      val oops = List(
      () => lf.push(1),
      () => lf.push(2),
      () => lf.push(3),
      //() => lf.contains(3)
      )
      def result(res: List[Any]) = {        
        (contains(3, lf, 3), s"should contain 3 ")}
      (oops,result)
    })
  }

  def contains(e: Int, lf:SchedulableLockFreeStack[Int], cap: Int): Boolean = {
    for (i <- 0 until cap) {      
      if (lf.pop() == e) {       
        return true
      }     
    }
    false
  }
}
