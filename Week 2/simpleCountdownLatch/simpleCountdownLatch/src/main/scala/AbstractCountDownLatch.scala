package simpleCountdownLatch

import instrumentation.monitors._

abstract class AbstractCountDownLatch extends Monitor {
  protected var v: Int = 0
  
  def count_=(i: Int) = v = i
  def count: Int = v
  def countDown(): Unit
}