package monitorBlocking
import instrumentation.monitors._
import instrumentation._

class SchedulableMonitorBlocking[E](capacity: Int, schedr: Scheduler) extends 
MonitorBlockingQueue[E](capacity) with SchedulableMonitor{
  val scheduler = schedr
  import scheduler._
  
  override def put(e:E) : Unit = {
    exec{super.put(e)}(s"")
  }
  override def take(): E = {
    exec{super.take()}(s"")
  }
  
}
