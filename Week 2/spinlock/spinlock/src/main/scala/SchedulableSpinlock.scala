package spinlock
import instrumentation.monitors._
import instrumentation._

class SchedulableSpinlock(sched: Scheduler) extends SpinlockMain with SchedulableMonitor{
  val scheduler = sched
  import scheduler._
  
  override def get(): Unit = {
    exec{super.get()}("Acquiring lock")
  }
  override def register(): String = {
    exec{super.register()}("Releasing lock")
  }
}