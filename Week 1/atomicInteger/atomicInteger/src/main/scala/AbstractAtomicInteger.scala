package atomicInteger
import instrumentation.monitors.Monitor
/*
* Do not modify this file. Only used for 
* code instrumentaion. Tasks are in SimpleAtomicInteger.scala
*/
abstract class AbstractAtomicInteger extends Monitor{
  protected var v: Int = 0
  
  def value_=(i: Int): Unit = v = i
  def value: Int = v
  def compareAndSet(expect: Int, update: Int): Boolean
  def get: Int
  def set(i: Int): Unit
}
