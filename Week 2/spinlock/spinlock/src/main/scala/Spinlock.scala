package spinlock
import instrumentation.monitors.Monitor
import atomicInteger.SimpleAtomicInteger

/* One way of enforcing limits on access to a shared resource in a multi-threaded 
 * execution environment is using locks. A lock is an important low-level synchronization
 * mechanism used to enforce mutual exclusion and concurrency control policy. 
 * 
 * A spinlock is a lock which causes a thread trying to acquire it to simply wait in a 
 * loop ("spin") while repeatedly checking if the lock is available.
 *    
 * Task: In this exercise, we implement a simple spinlock with lock and unlock methods.
 * we will assume we have an implementation of a simple atomic integer with a compare 
 * set method and base our spinlock implementation on it. The compare and set method is 
 * defined as compareAndSet(expect: Int, update: Int): Boolean.  
 *
 * Hint: refer and use compareAndSet(expect: Int, update: Int): Boolean method from 
 * past exercises 
*/

class Spinlock extends Monitor{
   /* Zero means the Spinlock is unlocked, while non-zero means it is locked. */
    val flag = new SimpleAtomicInteger(0)
    // Do not add other variables

    def lock(): Unit = {
       while(!flag.compareAndSet(0,1)){}
    }

    def unlock(): Unit = this.synchronized {
       flag.set(0)
    }
}

/*
class SimpleAtomicInteger(initValue: Int) extends AbstractAtomicInteger {
  value = initValue

  def compareAndSet(expect: Int, update: Int): Boolean = this.synchronized {
    if (value == expect) {
            set(update);
            return true;
        }
        else {
            return false;
        }
  }

  def get: Int = {
    return value
  }

  def set(newValue: Int): Unit = this.synchronized {
    value = newValue
  }
}
*/