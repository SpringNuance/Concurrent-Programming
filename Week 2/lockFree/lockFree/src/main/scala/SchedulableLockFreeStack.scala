package lockFree
import instrumentation.monitors._
import instrumentation._

class SchedulableLockFreeStack[E](capacity: Int, schedr: Scheduler) extends
LockFreeStack[E](capacity) with LockFreeMonitor{
  val scheduler = schedr
  import scheduler._
  
  override def push(e: E) = {
    exec{super.push(e)}(s"Inserting node $e")
  }
  override def pop(): E = {
    exec{super.pop()}(s"Removing a node")
  }
}
