package semaphoreBlockQueue
import instrumentation.monitors._
import instrumentation._

class SchedulableSemaphoreBlocking[E](capacity: Int, schedr: Scheduler) extends 
SemaphoreBlockingQueue[E](capacity) with SchedulableMonitor {
  val scheduler = schedr
  import scheduler._
  
  override def put(e: E) = {    
     exec{super.put(e)}(s"Going to add an element to the queue")
  }
  override def take(): E = {    
    exec{super.take()}(s"Trying to take an element from a queue")
  }
 
}