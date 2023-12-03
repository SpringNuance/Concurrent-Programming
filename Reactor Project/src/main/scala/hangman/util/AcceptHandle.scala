package hangman.util

import java.io.IOException
import java.net.{ServerSocket, Socket}

import reactor.api.Handle

class AcceptHandle(port: Option[Int] = Option.empty) extends Handle[Socket] {
  port.map(p => require(p > 0 && p < 65535))

  private val serverSocket: ServerSocket = new ServerSocket(port.getOrElse(0))
  System.out.println(serverSocket.getLocalPort) //the only allowed print to 'out'!

  override def read(): Socket = {
    try {
      serverSocket.accept()
    } catch {
      case _: IOException => null
    }
  }

  def close(): Unit = {
    try {
      serverSocket.close();
    } catch {
      case _:Throwable =>
    }
  }

}
