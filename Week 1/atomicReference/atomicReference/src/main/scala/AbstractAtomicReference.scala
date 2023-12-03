package atomicReference
import instrumentation.monitors._

/*
* Do not modify this file. Only used for 
* code instrumentaion. Tasks are in SimpleAtomicReference.scala
*/

abstract class AbstractAtomicReference[V] extends Monitor{
  protected var v: V = 0.asInstanceOf[V]
  
  def value_=(i: V):Unit = v = i
  def value: V = v
  def compareAndSet(expect: V, update: V): Boolean
  def get: V
  def set(i: V): Unit
}
