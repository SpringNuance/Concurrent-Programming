package atomicInteger
import instrumentation.monitors._
import instrumentation._

/*
* Do not modify this file. Only used for 
* code instrumentaion. Tasks are in SimpleAtomicInteger.scala
*/

class SchedulableAtomicInteger(init: Int, schedr: Scheduler) extends
  SimpleAtomicInteger(init) with SchedulableMonitor{
    val scheduler = schedr
    import scheduler._
    
    override def get: Int = {
      exec(v)(s"Retrieving the value")
    }
    override def set(i: Int): Unit = {
      exec{v = i}("Setting new value")
    }
  
}
