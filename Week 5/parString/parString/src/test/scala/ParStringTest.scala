package parString
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.parallel._

class ParStringTest extends AnyFlatSpec with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
  }

  "ParString" should "extend the correct type(ParSeq)" in {
    val txt = "A custom txt" * 25
    val partxt = new ParString(txt)  
    assert(partxt.isInstanceOf[immutable.ParSeq[Char]])
  } 
  "ParString" should "give the correct count result" in {
    val txt = "A custom text " * 25
    val upperSeqCnt = txt.foldLeft(0)((n, c) => if (Character.isUpperCase(c)) n + 1 else n)
    val partxt = new ParString(txt)
    val upperCnt = partxt.foldLeft(0)((n, c) => if (Character.isUpperCase(c)) n + 1 else n)
    assert(upperCnt == upperSeqCnt && partxt.isInstanceOf[immutable.ParSeq[Char]])
  }
  "ParString" should "give the correct filter result" in {
    val txt = "A custom txt" * 25
    val filteredSeqText = txt.filter(_ != ' ') 
    val partxt = new ParString(txt)
    val filteredText = partxt.filter(_ != ' ') 
    assert(filteredText.seq.toString == filteredSeqText && partxt.isInstanceOf[immutable.ParSeq[Char]]) 
  }
}