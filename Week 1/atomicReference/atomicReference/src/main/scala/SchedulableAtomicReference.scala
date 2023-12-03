package atomicReference
import instrumentation.monitors._
import instrumentation._

/*
* Do not modify this file. Only used for 
* code instrumentaion. Tasks are in SimpleAtomicReference.scala
*/

class SchedulableAtomicReference[V](init: V, schedr: Scheduler) extends
  SimpleAtomicReference[V](init) with SchedulableMonitor{
    val scheduler = schedr
    import scheduler._
    
    override def get: V = {
      exec(v)(s"Retrieving the value")
    }
    override def set(i: V): Unit = {
      exec{v = i}("Setting new value")
    }
  
}
