package hangman.util

import java.io.{BufferedReader, IOException, InputStreamReader, PrintStream}
import java.net.Socket

import reactor.api.Handle

class TCPTextHandle(private val socket: Socket) extends Handle[String] {
  require(socket != null)
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
  private val out = new PrintStream(socket.getOutputStream)

  override def read(): String = {
    try{
      in.readLine()
    } catch {
      case _: IOException => null
    }
  }

  def write(s: String): Unit = {
    out.println(s);
    out.flush()
  }

  def close(): Unit = {
    try {
      socket.close();
    } catch {
      case _: Throwable =>
    }
  }

}
