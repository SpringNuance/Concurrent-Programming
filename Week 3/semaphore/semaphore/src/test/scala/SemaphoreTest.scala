package semaphore
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

class SemaphoreTest extends AnyFlatSpec{
  val semaphore = new Semaphore(1)
  val remain = semaphore.availablePermits()
  println(s"permits before: $remain")
  "A release method" should "Increase permits by 1" in {
    semaphore.acquire()
    println(s"permits after: $remain")
  }
}