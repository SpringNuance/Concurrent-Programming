package supervisor

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.actor.OneForOneStrategy._
import scala.concurrent.duration._
import scala.language.postfixOps

/* 
  Implement the missing parts of the actor Supervisor below that 
  creates a child of type A called child, implements a sane 
  supervisor strategy and terminates if the child actor terminates. 
*/

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
