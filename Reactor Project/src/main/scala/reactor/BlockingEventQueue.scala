// 887799 Nguyen Xuan Binh

package reactor

import reactor.api.Event
import scala.collection.mutable._

// The semaphore class to implement the blocking queue
class Semaphore(private val capacity: Int) {
  var permits = capacity
  // Acquire a permit
  def acquire(): Unit = this.synchronized {
    while (permits == 0) {
        if (Thread.interrupted()) {
          /* Interrupted exception thrown if the thread is 
             interrupted by Thread.interrupt() (before or during the method invocation) */
          throw new InterruptedException(); 
        }
      this.wait()
    }
      permits -= 1
  }
  
  // release a permit
  def release(): Unit = this.synchronized { 
    permits += 1
    this.notify() // Efficiency notation: semaphore guarantees to wake up the necessary threads
  }
  
}

// Semaphore will be used for the blocking event queue
final class BlockingEventQueue[T] (private val capacity: Int) {
  
  /* The Blocking queue that holds the element events of this BlockingEventQueue[T]. */
  val queue = Queue[Event[T]]()
  
  /* Initialize semaphores, one with open slots counting (capacitySem) and one with full slots counting (elementSem). */
  var elementSem = new Semaphore(0)
  var mutexSem = new Semaphore(1)
  var capacitySem = new Semaphore(capacity)

  // Enqueuing method of the blocking event queue
  @throws[InterruptedException]
  def enqueue[U <: T](e: Event[U]): Unit = {
    if (e != null){ // The queue should not accept a null event, but it can accept event with a null data
      
      capacitySem.acquire() // waits on open slots
          mutexSem.acquire()
        queue.enqueue(e.asInstanceOf[Event[T]]); // Critical section. Typecast: Event[U] is typecasted as Event[T] and then the event is enqueued
         mutexSem.release()
      elementSem.release() // snotify full slots
    }
  }
  
  // Dequeueing method of the blocking event queue
  @throws[InterruptedException]
  def dequeue: Event[T] = {
    elementSem.acquire() // wait on full slots
    mutexSem.acquire()
    var element = queue.dequeue() // Critical section
    mutexSem.release()
    capacitySem.release() // notify open slots
    return element 
  }

  def getAll: Seq[Event[T]] = { 
    // The sequence where the data extracted from the queue will be appended
    elementSem.acquire() 
    var sequence = scala.collection.mutable.Seq[Event[T]]() 
    while (getSize != 0) { 
      var item = queue.dequeue() // Gradually empty the Queue
      sequence = sequence :+ item // Gradually populate the sequence
    }
    // Efficiency: getAll() should notify enqueuing threads number of times at most the size of the queue.
    for (i <- 0 until queue.size){
      this.notify()
    }
    capacitySem.release()
    return sequence
  }

  def getSize: Int = {
     if (Thread.interrupted()) 
      throw new InterruptedException(); 
    mutexSem.acquire()
    var size = queue.size
    mutexSem.release()
    return size
  }

  def getCapacity: Int = {
    return capacity; // return maximum capacity of the queue
  }

}
