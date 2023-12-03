package reactor

import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.time.{Seconds, Span}
import reactor.api.{EventHandler, Handle}

object ReactorSemanticsTest {
  val TEST_STRING_LENGTH = 1000
}

class ReactorSemanticsTest extends AnyFunSuite with TimeLimitedTests {

  //The time limit is arbitrary and dependent on the computer
  override def timeLimit: Span = Span(10, Seconds)

  def randomString(n: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    val builder = new StringBuilder
    for (_ <- 1 to n) {
      val next = util.Random.nextInt(chars.length)
      builder.append(chars(next))
    }
    builder.toString
  }

  class TestContext(val d: Dispatcher) {
    val content: String = randomString(ReactorSemanticsTest.TEST_STRING_LENGTH)
    val handle: TextHandle = new TextHandle(content)
    val handler: TextHandler = new TextHandler(this)
    val buffer: StringBuilder = new StringBuilder()

    def receive(s: String): Unit = {
      if(s != null) {
        buffer.append(s)
      } else {
        d.removeHandler(handler)
      }
    }

    def received: String = {
      buffer.toString
    }
  }

  class TextHandle(val txt: String) extends Handle[String] {
    var index: Int = 0

    override def read(): String = {
      index match {
        case x if x < txt.length => { index += 1; "" + txt.charAt(index-1) }
        case x if x == txt.length => { index += 1; null }
        case x if x > txt.length => fail("handle read after returning null")
      }
    }
  }

  class TextHandler(val c: TestContext) extends EventHandler[String] {
    override def getHandle: Handle[String] = { c.handle }
    override def handleEvent(evt: String): Unit = { c.receive(evt) }
  }

  test("the dispatcher is created and destroyed without hanging") {
    val d = new Dispatcher()
  }

  test("handleEvents with no handlers returns immediately") {
    val d = new Dispatcher()
    d.handleEvents()
  }

  test("the dispatcher works for one handler") {
    val d = new Dispatcher()

    val context = new TestContext(d)
    d.addHandler(context.handler)
    d.handleEvents()

    assertResult(context.content) { context.buffer.toString() }
  }

  test("the dispatcher works for multiple handlers without mangling events") {
    val d = new Dispatcher()

    val c1 = new TestContext(d)
    val c2 = new TestContext(d)
    val c3 = new TestContext(d)
    d.addHandler(c1.handler)
    d.addHandler(c2.handler)
    d.addHandler(c3.handler)
    d.handleEvents()

    assertResult(c1.content) { c1.received }
    assertResult(c2.content) { c2.received }
    assertResult(c3.content) { c3.received }
  }

  test("a dispatcher with a small queue works for multiple handlers") {
    val d = new Dispatcher(1)

    val c1 = new TestContext(d)
    val c2 = new TestContext(d)
    val c3 = new TestContext(d)
    d.addHandler(c1.handler)
    d.addHandler(c2.handler)
    d.addHandler(c3.handler)
    d.handleEvents()

    assertResult(c1.content) { c1.received }
    assertResult(c2.content) { c2.received }
    assertResult(c3.content) { c3.received }
  }

  test("two distinct dispatchers are independent") {
    val d1 = new Dispatcher()
    val d2 = new Dispatcher()

    val c1 = new TestContext(d1)
    val c2 = new TestContext(d2)
    d1.addHandler(c1.handler)
    d2.addHandler(c2.handler)

    d1.handleEvents()
    d2.handleEvents()

    assertResult(c1.content) { c1.received }
    assertResult(c2.content) { c2.received }
  }

}
