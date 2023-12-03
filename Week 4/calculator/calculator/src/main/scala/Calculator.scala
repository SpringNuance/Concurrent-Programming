package calculator

import akka.actor._

abstract class Operation
// assigns value(v) to state (current state) 
case class Assign(v: Double) extends Operation
// adds a value(v) to state
case class Add(v: Double) extends Operation
// multiplies state by value(v)
case class Multiply(v: Double) extends Operation
// keep state the same if it is b/n l and u inclusive.
// assign l to state if state is less than l
// assign u to state if state is bigger than u 
case class Clamp(l: Double, u: Double) extends Operation  

/* Implement a calculator that has an initial state value of zero, handles 
   the operations\messages (Assign, Add, Multiply, Clamp) listed above and 
   sends the current state value back to the message sender. 
*/

class Calculator extends Actor {
  var state: Double = 0

  def receive = {
    case Assign(v) => state = v
    case Add(v) => state += v
    case Multiply(v) => state *= v
    case Clamp(l, u) => {
      if (state < l) {
        state = l
      } else if (state > u){
        state = u
      }
    }
      sender() ! s"Current state is: $state"
  }
}
