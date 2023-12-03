package spinlock

class SpinlockMain extends Runnable {
  val lock = new Spinlock
  
  def get() : Unit = {
    lock.lock()
    println(Thread.currentThread().getId)
    register()
    lock.unlock()
  }
  def register(): String = {
    lock.lock()
    println(Thread.currentThread().getName)
    lock.unlock()
    Thread.currentThread().getName
  }
  override def run(): Unit = {
    get()
  }
}
object SpinlockMain extends App{
  val test = new SpinlockMain
  new Thread(test).start()
  new Thread(test).start()
  
}
