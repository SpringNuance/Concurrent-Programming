package atomicReference
/* One issue that needs to be noted when implementing concurrent
 * programs is memory consistency and errors associated with it. We 
 * say memory consistency error occured when multiple execution units
 * operating on a shared memory have inconsistent view of a given 
 * shared variable. There are a lot of reasons why these inconsistencies 
 * happen which include caching and compiler optimizations. The key to 
 * avoid memory concistency problems is to understand and insure happens-before
 * relationship such that if a happens-before b, the effects of a will be visible 
 * to b.
 *    
 * Task: In this exercise we implement a simple atomic reference; a container to a value 
 * that may be updated atomically and provides a happens-before relationship when
 * reading and writing a value.  
 *
 * Hint: You may use synchronized key word to implement happens-before 
*/

class SimpleAtomicReference[V](initValue: V) extends AbstractAtomicReference[V] {
  value = initValue

  def compareAndSet(expect: V, update: V): Boolean = this.synchronized {
    if (value == expect) {
            set(update);
            return true;
        }
        else {
            return false;
        }
  }

  def get: V = {
    return value
  }

  def set(newValue: V): Unit = this.synchronized {
    value = newValue
  }
}

/*
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