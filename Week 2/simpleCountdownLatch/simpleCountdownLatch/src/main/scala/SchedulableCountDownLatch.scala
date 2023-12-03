package simpleCountdownLatch

import instrumentation.monitors._
import instrumentation._

class SchedulableCountDownLatch(initCount: Int, sched: Scheduler) 
extends SimpleCountDownLatch(initCount) with SchedulableMonitor {
  val scheduler = sched
  import scheduler._
  
  override def count_=(i:Int)={
    sched.exec{v = i}(s"Initializing counter")
  }
  override def count = {
    sched.exec{v}(s"extracting the value of counter")
  }
  override def await() = {
    exec{super.await()}(s"waiting")
    if (count > 0) { throw new Exception("Waiting failed!") }
  }
  
}