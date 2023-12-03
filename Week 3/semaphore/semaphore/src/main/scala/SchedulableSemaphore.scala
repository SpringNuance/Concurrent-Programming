package semaphore
import instrumentation.monitors._
import instrumentation._

class SchedulableSemaphore(capacity: Int, val scheduler: Scheduler) extends 
Semaphore(capacity) with SchedulableMonitor{
  
  import scheduler._
  
  override def acquire(): Unit = {
    exec{super.acquire()}("")
  }
  override def release(): Unit = {
    exec{super.release()}("")
  }
}