package calculator
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit,TestActorRef}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuiteLike
import scala.concurrent.duration._
import org.scalatest.Assertions._

class TestCalculator extends TestKit(ActorSystem("TestCalculator"))
with ImplicitSender with Matchers with AnyFunSuiteLike{

   val calc = TestActorRef[Calculator]
   calc.underlyingActor.state = 5
   
  test("The assign call should return the value given"){
    val calc = TestActorRef[Calculator]    
     calc ! Assign(2.0)     
     assert(calc.underlyingActor.state == 2.0)
  }
 test("Addition should produce the correct value"){
   val calc = TestActorRef[Calculator]
   calc.underlyingActor.state = 5
   calc ! Add(4)
   assertResult(9){calc.underlyingActor.state}
 }
 test("Multiplication should produce the correct value"){   
   calc ! Multiply(4)
   assertResult(5*4){calc.underlyingActor.state}
 }
 test("Clamp should give the state value between l and u"){   
   calc ! Clamp(4,6)
   calc.underlyingActor.state = 5
   assertResult(5){calc.underlyingActor.state}
 }
 test("should give correct answer if state value is less than l"){
   calc ! Clamp(6, 10)
   assertResult(6){calc.underlyingActor.state}
 }
 test("should give correct answer if state value is greater than u"){
   calc ! Clamp(1, 4)
   assertResult(4){calc.underlyingActor.state}
 }
}