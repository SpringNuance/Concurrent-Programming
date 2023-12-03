package lifecycle

import akka.actor.{ ActorSystem, Props }
import akka.testkit._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuiteLike
import scala.concurrent.duration._
import org.scalatest.Assertions._
import akka.actor.SupervisorStrategy._
import akka.actor._
import scala.concurrent.Await
import scala.language.postfixOps


class Sink extends Actor{
  var state = ""

  def receive = {
    case 0 => sender() ! state
    case msg: String => state += msg
  }
}

class TestLifeCycler extends TestKit(ActorSystem("TestLifeCycler")) with ImplicitSender
  with Matchers with AnyFunSuiteLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    shutdown(system)
  }

  test("LifeCycler's child should print  SCALA "){
    val sink = TestActorRef(new Sink, name = "Sink")
    val lc = TestActorRef(new LifeCycler[Other](sink), name = "LifeCycler")
    val child = lc.underlyingActor.child

    val probe = TestProbe()
    probe.watch(child)
    probe.expectTerminated(child, 2 seconds)

    sink ! 0
    expectMsg("SCALA")
  }
}