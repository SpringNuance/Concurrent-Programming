package atomicInteger
/* One issue that needs to be noted when implementing concurrent
 * programs is memory consistency and errors associated with it. We 
 * say memory consistency error occured when multiple execution units
 * operating on a shared memory have an inconsistent view of a given 
 * shared variable. There are a lot of reasons why these inconsistencies 
 * happen which include caching and compiler optimizations. The key to 
 * avoid memory concistency problems is to understand and insure happens-before
 * relationship such that if a happens-before b, the effects of a will be visible 
 * to b.
 *    
 * Task: In this exercise we implement a simple atomic integer; an integer value 
 * that may be updated atomically and provides a happens-before relationship when
 * reading and writing an integer value.  
 *
 * Hint: You may use synchronized key word to implement happens-before 
*/
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