package spinlock

class SpinlockMain1 extends Runnable {
  val lock = new Spinlock
 
  def get(): String = {
    lock.lock()
    println(Thread.currentThread().getId)
    register()
    lock.unlock()
    Thread.currentThread().getName
  }
  def register(): Unit = {    
    //lock.lock()
    println(Thread.currentThread().getName)
    //lock.unlock()
  }
  override def run(): Unit = {
    get()
  }
}


