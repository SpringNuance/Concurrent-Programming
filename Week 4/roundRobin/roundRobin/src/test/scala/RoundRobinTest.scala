package roundRobin

import akka.actor._
import akka.testkit.TestActor
import akka.testkit._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuiteLike


class Child extends Actor{
  def receive = {
     case msg =>
       sender() ! msg
  }  
}

class RoundRobinTest extends TestKit(ActorSystem("RoundRobin")) with
ImplicitSender with AnyFunSuiteLike with BeforeAndAfterAll{
  override def afterAll(): Unit = {
    shutdown(system)
  }
   
  test("Should echo the forwarded message back") {
    val rr = TestActorRef(new RoundRobin[Child](1))
    val children = rr.underlyingActor.children   
    rr ! "hello"
    expectMsg("hello") 
  } 
  test("Should create numChildren children") {
    val numChildren = 5
    val rr = TestActorRef(new RoundRobin[Child](numChildren))
    val children = rr.underlyingActor.children
    assert(children.length == numChildren)
  }
  test("Should forward messages to children in round robin manner") {
    val numChildren = 5
    val rr = TestActorRef(new RoundRobin[Child](numChildren))
    val children = rr.underlyingActor.children
    for (i <- 0 until children.length) {
      rr ! i
      expectMsg(i)
    }

  } 
}