package supervisor
import akka.actor._
import akka.testkit._
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuiteLike
import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps


class Child extends Actor{
  var state = "started"

  override def postRestart(reason: Throwable): Unit = {
    state = "restarted"
  }

  def receive = {
    case 0 => throw new TransientException()
    case 1 => throw new CorruptedException()
    case 2 => sender() ! state
  }
}

class SupervisorTest extends TestKit(ActorSystem("SupervisorTest")) with
ImplicitSender with AnyFunSuiteLike with BeforeAndAfterAll {
  
  override def afterAll(): Unit = {
    shutdown(system)
  }

  test("Supervisor should create child of type A named Child"){
    val supervisor = TestActorRef(new Supervisor[Child], name = "Supervisor1")
    val child = supervisor.underlyingActor.child
    child ! 2
    expectMsg("started")
  }

  test("Supervisor should implement a sane strategy of resuming for a temporary problem"){
    val supervisor = TestActorRef(new Supervisor[Child], name = "Supervisor2")
    val child = supervisor.underlyingActor.child
    child ! 2
    expectMsg("started")
    child ! 0
    child ! 2
    expectMsg("started")    
  }

  test("Supervisor should implement a sane strategy of restarting for a permanent problem"){    
    val supervisor = TestActorRef(new Supervisor[Child], name = "Supervisor3")
    val child = supervisor.underlyingActor.child
    child ! 2
    expectMsg("started")
    child ! 1
    child ! 2
    expectMsg("restarted")   
  }

  test("Supervisor should terminate itself upon death of a child"){
    val probe = TestProbe()
    val supervisor = TestActorRef(new Supervisor[Child], name = "Supervisor4")
    val child = supervisor.underlyingActor.child

    probe.watch(supervisor)
    child ! PoisonPill
    probe.expectTerminated(supervisor, 2 seconds)
  }
  
}