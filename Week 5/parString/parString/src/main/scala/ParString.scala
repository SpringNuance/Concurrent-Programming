package parString
import scala.collection.parallel._


/* 
 * In the earlier modules, we have seen ways of composing multiple threads of computation 
 * into safe concurrent programs. In this exercise, we will briefly focus on achieving better 
 * performance while requiring minimal changes in the organization of legacy programs. One 
 * specific way of achieving better performance is data parallelism which relies on doing computation
 * in parallel on a different portion of a data collection. 
 *
 * 
 * Task: In this task, we implement a custom parallel collection called ParString which is a counter 
 * part of the Java's String class that supports parallel operations:
 * 
 * class ParString(val str: String) extends immutable.ParSeq[Char] {
 *  def apply(i: Int)
 *  def length
 *  def seq
 *  def splitter
 *  override def newCombiner
 * }
 *
 * A parallel string is a sequence of characters, so it is only logical to extend the ParSeq parallel collection 
 * trait with the Char type argument. When we extend a parallel collection, we need to implement its apply, 
 * length, splitter, and seq methods. Follow the Hint links below for more detail.       
 *
 * Hint: Consult chapter 5 of the book: Learning concurrent programming in Scala by Aleksandar Prokopec and/or 
 * https://docs.scala-lang.org/overviews/parallel-collections/custom-parallel-collections.html
*/


class ParString(val str: String) extends immutable.ParSeq[Char] {
  def apply(i: Int) = str.charAt(i)

  def length = str.length
  
  def seq = new collection.immutable.WrappedString(str)
  
  def splitter = new ParStringSplitter(str, 0, str.length)

  override def newCombiner = new ParStringCombiner

  class ParStringCombiner extends Combiner[Char, ParString] {
private val chunks = new scala.collection.mutable.ArrayBuffer += new StringBuilder
private var lastc = chunks.last
var size = 0
def addOne(elem: Char) = {
  lastc += elem
  size += 1
  this
}
def combine[N <: Char, NewRepr >: ParString](that: Combiner[N, NewRepr]) = {
  if (this eq that) this else that match {
    case that: ParStringCombiner =>
    size += that.size
    chunks ++= that.chunks
    lastc = chunks.last
    this
  }
}
def result(): ParString = {
  val rsb = new StringBuilder
  for (sb <- chunks) rsb.append(sb)
  new ParString(rsb.toString)
}
def clear(): Unit = {
  print("Hello")
}
  }

  class ParStringSplitter(val s: String, var i: Int, val limit: Int) extends SeqSplitter[Char] {
    final def hasNext = i < limit
    def next() = {
    val r = s.charAt(i)
    i += 1
     r
    }
    def dup = new ParStringSplitter(s, i, limit)
    def remaining = limit - i
    def split = {
      val rem = remaining
      if (rem >= 2) psplit(rem / 2, rem - rem / 2)
      else Seq(this)
    }
    def psplit(sizes: Int*): Seq[ParStringSplitter] = {
      val ss = for (sz <- sizes) yield {
      val nlimit = (i + sz) min limit
      val ps = new ParStringSplitter(s, i, nlimit)
      i = nlimit
      ps
    }
      if (i == limit) ss
      else ss :+ new ParStringSplitter(s, i, limit)
    }
  }

}
