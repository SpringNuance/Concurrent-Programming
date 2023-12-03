package lifecycle
import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

class ExceptionA extends RuntimeException {}
class ExceptionB extends RuntimeException {}

class Other(sink: ActorRef) extends Actor {
  var flag = false

  override def preStart() = {
    print("S")
    sink ! "S"
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    if (flag){
      print("A")
      sink ! "A"
    } else {
      print(" ")
      sink ! " "
    }
  }

  override def postRestart(reason: Throwable) = {
    print("L")
    sink ! "L"
  }

  override def postStop() = {
    print("A")
    sink ! "A"
  }

  def receive = {
    case 0 => throw new ExceptionA()
    case 1 =>
      flag = true
      print("C")
      sink ! "C"
      throw new ExceptionB()
  }
}
