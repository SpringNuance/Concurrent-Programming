package roundRobin
import akka.actor._

/* In this exercise, we implement an actor called RoundRobin declared as  
class RoundRobin[A <: Actor : scala.reflect.ClassTag](numChildren: Int) extends Actor. 
This actor should be able to create a number of children (numChildren) and enqueue them 
in an internal queue called children. For each message it receives, the RoundRobin actor 
should forward the message to the child found at the head of the children queue and 
move (enqueue) that child to the tail of the queue. The exercise teaches about 
creating children and forwarding messages.
*/

class RoundRobin[A <: Actor: scala.reflect.ClassTag](numChildren: Int) extends Actor {
  if (numChildren <= 0)
    throw new IllegalArgumentException("numChildren must be positive")
  
  var children = collection.immutable.Queue[ActorRef]()

  override def preStart() = {
    for (i <- 0 until numChildren){
      val child = context.actorOf(Props[A])
      children = children.enqueue(child)
    }
  }

  def receive = {
    case msg => 
      var child = children.dequeue 
      child._1.forward(msg)
      children.enqueue(child)
  }

}

/*
class LifeCycler[A <: Actor: scala.reflect.ClassTag](sink: ActorRef) extends Actor {
  // TODO: Implement class
  val child = context.actorOf(Props(classOf[Other], sink), "child")
  override def preStart() = {
    context.watch(child)
    child! 1
    child! 0
    child! PoisonPill
  }
  override val supervisorStrategy =  OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 milli){
      case _: ExceptionA     => Restart
      case _: ExceptionB     => Resume
  }
  def receive = {
    case Terminated(child) => self! PoisonPill
  }
}


/** An exception thrown by the child for a temporary problem. */
class TransientException extends Exception {}
/** An exception thrown by the child to indicate a corrupted state (serious problem). */
class CorruptedException extends Exception {}
class Supervisor[A <: Actor: scala.reflect.ClassTag] extends Actor {
  // TODO: Implement class
  // TODO: Create a child actor of type A called child
  val child: ActorRef = context.actorOf(Props[A], "child") 
  // TODO: Start watching the child in the preStart 
  override def preStart() = {
    context.watch(child)
  }
  // TODO: Implement a sane supervisor strategy.
  // Resume if child throws TransientException (is having a temporary problem)
  // Restart if child throws CorruptedException (is having a serious problem) 
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second){
    case _:TransientException => Resume
    case _:CorruptedException => Restart
}
  
  // TODO: Terminate if child terminates 
  def receive = {
    case Terminated(child) =>
      self ! PoisonPill
  }
}
*/