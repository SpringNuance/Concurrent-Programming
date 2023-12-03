// 887799 Nguyen Xuan Binh

package reactor

import reactor.api.{Event, EventHandler}
import scala.collection.mutable._

// Note: Blocking event queue and semaphore code are under dispatcher's part code

final class Dispatcher(private val queueLength: Int = 10) {
  require(queueLength > 0)
 // The blocking queue that stores the events. Due to some type problem, 
 // the type of this queue is Any instead of Event[Any]
 var blockingEventQueue = new BlockingEventQueueDispatcher[Any](queueLength)

 // The map that maps the eventHandler as keys to its Working threads
 // That is, each eventHandler is only being managed by one working thread 
 var registeredEventHandlers = scala.collection.mutable.Map[EventHandler[_], WorkerThread[_]]()

 // The array buffer that stores the deregistered EventHandlers. The dispatcher will not accept 
 // second registration of event handlers stored in this array
 var deregisteredEventHandlers = scala.collection.mutable.ArrayBuffer[EventHandler[_]]()
 
  @throws[InterruptedException]
  def handleEvents(): Unit = {
    /* This while loop will keep the system run continuously to receive events 
    from registered event handlers. It only stops when all event handlers are deregistered*/
    while (!registeredEventHandlers.isEmpty) {
	       if (Thread.interrupted()) {
          /* Interrupted exception thrown if the thread is 
             interrupted by Thread.interrupt() (before or during the method invocation) */
          throw new InterruptedException()
        }
      /* Wait for select to return an event */
      var event = select
      // Events must not be read by the Reactor from a handle before registration
      if (registeredEventHandlers.contains(event.getHandler))
      //The event is handled by its own eventHandlers() 
			  event.handle()
		} 
  }

@throws[InterruptedException]
  def select[_]: Event[_] = {
    if (Thread.interrupted()) {
          /* Interrupted exception thrown if the thread is 
             interrupted by Thread.interrupt() (before or during the method invocation) */
          throw new InterruptedException()
    }
    // Getting an event read and enqueued by the working thread and forward it to handleEvents 
    // Waiting for message is done by the working thread
    return blockingEventQueue.dequeue
  }

  def addHandler[T](h: EventHandler[T]): Unit = {
    // Each handler may be registered and deregistered only once
    // If the event handler is already working in registration, or has been deregistered, this method does nothing
    if (!deregisteredEventHandlers.contains(h) && !registeredEventHandlers.contains(h)){
      // Create an individual thread tailored only for this eventHandler. They will be a working pair
      var workingThread = new WorkerThread[Any](h.asInstanceOf[EventHandler[Any]], blockingEventQueue)
		  registeredEventHandlers(h) = workingThread // Add the eventHandler - workingThread to the map
		  workingThread.start() // Important: Starting the thread makes the handle read non-stop incoming messages. 
      // The threads always try to read message and enqueue the event into the blocking queue whenever possible
    }
  }

  def removeHandler[T](h: EventHandler[T]): Unit = {
    // If there is such event handler in the map, it can be de-registered
    if (registeredEventHandlers.contains(h)){
      var workingThread = registeredEventHandlers(h) // Getting the working thread of the event handler from the map
      // Check not to call the cancelThread() more than once on the already terminated working thread
      if (workingThread.check == false) workingThread.cancelThread(); 
      // Safely remove the event handler from the registered handlers
	    registeredEventHandlers.remove(h);
    }
  }
}


final class WorkerThread[_](eventHandler: EventHandler[Any], blockingQueue: BlockingEventQueueDispatcher[Any]) extends Thread {
  var check = false
  // run() method is responsible for reading message and enqueuing events
  override def run(): Unit = {
      while (!check) { // Continues to run non-stop to receive events. Only stop when cancelThread() is called
			var event = new Event[Any](eventHandler.getHandle.read(), eventHandler) // The event extracted from reading of the handle
      // Actual waiting for the message is in eventHandler.getHandle.read() => run() method of working thread will wait for message
      // on behalf of select and handleEvents method above
			/* Error or null message: terminating the thread */
        if (event.getEvent == null) check = true
        /* If the message is fine, the event will be enqueued into the blocking queue for the select method to dequeue */
        blockingQueue.enqueue(event)
		}
}

  def cancelThread(): Unit = {
    check = true // Turning check to true, ending the while loop in run() method
    // Interrupt the working thread, terminating any works in progress
    this.interrupt()
  }

}

////////////////////////////////////////////////////////////////////////////
/* 
Below this are just semaphore and blocking event queue implemeneted in task 1. 
Since I'm not sure whether I can omit the blocking queue code out of dispatcher task 2,
I include them in this dispatcher file as well
*/

// The semaphore class to implement the blocking queue
class SemaphoreDispatcher(private val capacity: Int) {
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
final class BlockingEventQueueDispatcher[T] (private val capacity: Int) {
  
  /* The Blocking queue that holds the element events of this BlockingEventQueue[T]. */
  val queue = Queue[Event[T]]()
  
  /* Initialize semaphores, one with open slots counting (capacitySem) and one with full slots counting (elementSem). */
  var elementSem = new SemaphoreDispatcher(0)
  var mutexSem = new SemaphoreDispatcher(1) 
  var capacitySem = new SemaphoreDispatcher(capacity)

  // Enqueuing method of the blocking event queue
  @throws[InterruptedException]
  def enqueue[U <: T](e: Event[U]): Unit = {
    if (e != null){ // The queue should not accept a null event, but it can accept event with a null data
      capacitySem.acquire() // waits on open slots
      mutexSem.acquire() // Mutual exclusion of same threads
      queue.enqueue(e.asInstanceOf[Event[T]]); // Critical section. Typecast: Event[U] is typecasted as Event[T] and then the event is enqueued
      mutexSem.release()
      elementSem.release() // snotify full slots
    }
  }
  
  // Dequeueing method of the blocking event queue
  @throws[InterruptedException]
  def dequeue: Event[T] = {
    elementSem.acquire() // wait on full slots
    mutexSem.acquire() // Mutual exclusion of same threads
    var element = queue.dequeue() // Critical section
    mutexSem.release()
    capacitySem.release() // notify open slots
    return element 
  }

  def getSize: Int = {
    if (Thread.interrupted()) 
      throw new InterruptedException(); 
    mutexSem.acquire() // Mutual exclusion of same threads
    var size = queue.size
    mutexSem.release()
    return size // Return size of the blocking queue
  }

  def getCapacity: Int = {
    return capacity; // return maximum capacity of the queue
  }

}