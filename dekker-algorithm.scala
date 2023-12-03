//Dekker's algorithm is a classic mutual exclusion algorithm for two threads. It uses three variables to implement a critical section and even provides freedom from starvation by having the threads take turns, if the critical section is contested.

//The algorithm is introduced in more detail in Ben-Ari's textbook, section 3.9, and actually proven correct in section 4.5. But will the algorithm work on your PC?

//Below is a Scala implementation of Dekker's algorithm for you to experiment with. The critical section for both threads is a simple +1 addition to a variable shared by both threads. If two threads add +1 to a number, let's say 50 000 times, the result should be 100 000 if the algorithm works and enforces mutual exclusion.

object MemoryDemo {

  var count: Int = 0

  var wantp: Boolean = false
  var wantq: Boolean = false
  var turn: Int = 1

  def test(target: Int): Unit = {

    val threadP: Thread = new Thread() {
      override def run(): Unit = {
        var i: Int = 0

        while (i < target) {
          wantp = true
          while (wantq) {
            if (turn == 2) {
              wantp = false
              while (turn != 1) {} //busy wait
              wantp = true
            }
          }
          //critical section starts
          count = count + 1
          //critical section ends
          turn = 2
          wantp = false
          i += 1
        }
      }
    }

    val threadQ: Thread = new Thread() {
      override def run(): Unit = {
        var i: Int = 0

        while (i < target) {
          wantq = true
          while (wantp) {
            if (turn == 1) {
              wantq = false
              while (turn != 2) { } //busy wait
              wantq = true
            }
          }
          //critical section starts
          count = count + 1
          //critical section ends
          turn = 1
          wantq = false
          i += 1
        }
      }
    }

    threadP.start()
    threadQ.start()

    threadP.join()
    threadQ.join()
  }

  def main(args: Array[String]): Unit = {
    val iterations: Int = 50000
    val correctCount: Int = 2 * iterations

    val tick: Long = System.currentTimeMillis()
    test(iterations)
    val duration: Long = System.currentTimeMillis() - tick

    Console.println(s"count should be $correctCount, was $count")
    Console.println(s"test took $duration milliseconds")
  }

}
//Save the program as MemoryDemo.scala, compile and run using:

//scalac MemoryDemo.scala
//scala MemoryDemo
//1) Will the program print the correct answer?

//2) Will the program always terminate?

//3) Add @volatile tags to the variables wantp, wantq and turn. How did the program behaviour change?

//4) Is it necessary to add the @volatile tag for the variable count? Why?

// Hint

//Dekker's algorithm assumes a sequentially consistent memory model, which is not the case a modern PC.