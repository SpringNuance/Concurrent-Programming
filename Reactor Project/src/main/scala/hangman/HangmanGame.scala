// 887799 Nguyen Xuan Binh

package hangman
import reactor.api.{EventHandler, Handle}
import hangman.util.{AcceptHandle, TCPTextHandle}
import reactor.Dispatcher
import reactor.WorkerThread
import java.net.{ServerSocket, Socket}
import java.io.IOException

/*
How to run the game:
Make sure sbt has been installed and telnet has been enabled
From the terminal, type "sbt" and enter
JVM starts, now type "run <word> <number of trials>" and enter. The port number is being generated
for example, "run concurrency 9"
Open windows powershell, then type telnet. Telnet remote server starts to run
Type in the powershell "open local host <port number>" where port number is obtained from above
The server will not respond with anything, but the hangman server has already started
You type in your name and hit enter
You can start playing Hangman game from now
*/

// The handler for TCPTextHandle.
class TCPTextHandler(tcpTextHandle: TCPTextHandle, hangmanGame: HangmanGame) extends EventHandler[String] {
  var player: Player = null

  // Return the tcpTextHandle
  def getHandle: Handle[String] = tcpTextHandle

  def handleEvent(string: String): Unit = {
  // The message is null. The player has left the game
  	if (string == null) {
	  tcpTextHandle.close() // close the end socket of the player
	  // Remove the text handler of this player from the network dispatcher
      hangmanGame.removeHandler(this.asInstanceOf[EventHandler[Any]]) 
	  // Remove the player from game
	  hangmanGame.removePlayer(player)
    } else {
		// Clean message without leading and trailing spaces
		var message = string.trim
	  if (player == null){ // If player is new, add them to the player list
		// Player is created and added to the game. In this case, message is the name of player
		// You should type only 1 word for your name
	    player = new Player(tcpTextHandle, message) 
	    hangmanGame.addNewPlayer(player)
		// Welcoming message and current game status shown to new player
		tcpTextHandle.write("Welcoming to the Hangman game! Below is the current game progress")
	    tcpTextHandle.write(hangmanGame.messageToNewPlayer)
		// If player is already registered, they are shown ongoing guesses of all players including themselves
	  } else hangmanGame.makeAGuess(message(0), player.getName) 
    }
  }
}

// The handler for AcceptHandle
class AcceptHandler(handle: Handle[Socket], hangmanGame: HangmanGame) extends EventHandler[Socket]{
  // Return the acceptHandle
  def getHandle = handle
  // Add the player tcpTextHandler to the game's dispatcher handlers
  def handleEvent(socket: Socket) {
    /* Create the TCPTextHandler with the socket created by being accepted by the acceptHandle
	 from its read() method */ 
	var tcpTextHandler: TCPTextHandler = new TCPTextHandler(new TCPTextHandle(socket), hangmanGame)
	// Add the TCPTextHandler of the player to the dispatcher
	hangmanGame.addHandler(tcpTextHandler.asInstanceOf[EventHandler[Any]])
  }
}

// Player of the game. Each player has only two properties: their name and their tcp socket
// Player interacts with the hangman game by entering message that is processed by the TCPTextHandler above
class Player(tcpTextHandle: TCPTextHandle, name: String) {
  def getPlayerTcpTextHandle = tcpTextHandle
  def getName = name
}

// The main class that operates the hangman game network
class HangmanGame(val hiddenWord: String, val initialGuessCount: Int) {

  // The game dispatcher to manage the concurrent network of players
  var dispatcher = new Dispatcher()

  // if this is set to true, the game will end and the server is turned off
  var finished = false

  // buffer that contains the current online players
  var playerList = scala.collection.mutable.ArrayBuffer[Player]()

  // game state that tracks the status of the game. 
  var gameState = new GameState(hiddenWord, initialGuessCount, Set[Char]())

  // Add the acceptHandler or TCPTextHandler to the game's dispatcher
  def addHandler(handler: EventHandler[Any]): Unit = {
	dispatcher.addHandler(handler)
  }

  // Remove the acceptHandler or TCPTextHandler from the game's dispatcher
  def removeHandler(handler: EventHandler[Any]): Unit = {
	dispatcher.removeHandler(handler)
  }

  // add new player to the game
  def addNewPlayer(player: Player): Unit = {
    playerList += player
  }

  // remove the player from the game
  def removePlayer(player: Player) ={
    playerList -= player
  }

  // Player make a guess, the game state is updated
  def makeAGuess(guessCharacter: Char, nameOfGuesser: String): Unit = {
	gameState = gameState.makeGuess(guessCharacter) // game state updated by the guess
	finished = gameState.isGameOver // check whether the game is finished
	// Status string of the latest guess made by that player
	var latestGuess: String = guessCharacter + " " + gameState.getMaskedWord + " " + gameState.guessCount + " " + nameOfGuesser
	// Send the status string to all players
	writeMessageToAllPlayers(latestGuess)
	if (finished) {
	// print game lost message to all players
	  if (gameState.isGameLost){
		writeMessageToAllPlayers("The game is lost. All of your combined efforts have failed")
	  } else if (gameState.isGameWon){
	// print game won message to all players
		writeMessageToAllPlayers("Congratulations. You have beaten the Hangman game!")
	  }
	// Safely ends the game and quit the server
	  endTheGame
    } 
  }

  // Write the given message to all players
  def writeMessageToAllPlayers(message: String): Unit = {
	for (player <- playerList) {
	  player.getPlayerTcpTextHandle.write(message)
	}
  }

  // The current game status sent to a newly registered player
  def messageToNewPlayer: String = {
	return gameState.getMaskedWord + " " + gameState.guessCount;
  }

  // When the game ends, all handlers must be closed and all working threads must be canceled
  def endTheGame: Unit = {
	// Close all handlers in the dispatcher
	for (pair <- dispatcher.registeredEventHandlers){
 	  if (pair._1.isInstanceOf[AcceptHandle]) {
		pair._1.asInstanceOf[AcceptHandle].close
	  } else if (pair._1.isInstanceOf[TCPTextHandle]){
		pair._1.asInstanceOf[TCPTextHandle].close
	  }
	// Cancel all working threads of the dispatchers
	  pair._2.cancelThread()
	}
	dispatcher.registeredEventHandlers.clear
  }
}

object HangmanGame {
  def main(args: Array[String]): Unit = {
  // word to be guessed in hangman is args(0)
  // number of trials for all identified players is args(1)
  // Create a new hangman game 
  var hangmanGame = new HangmanGame(args(0), args(1).toInt);
  // Create a random port number
  var port: Int = scala.util.Random.nextInt(65535)
  var acceptHandle: AcceptHandle = null
  try {
	acceptHandle = new AcceptHandle(Option(port)) // Try to create a new acceptHandle
  } catch {
    case error: IOException => System.out.print(error) // Catch if there is IOExeption. 
  }
  // Print to System.out the port number so it can be started on telnet
  System.out.print("The game server starts on the port " + port) 
  // Create a new accept handler with the accept handle created above
  var acceptHandler = new AcceptHandler(acceptHandle, hangmanGame);
  // Add the acceptHandler to the dispatcher of the game
  hangmanGame.dispatcher.addHandler(acceptHandler);
  // Dispatcher of the game start to handle events. The game start running
  hangmanGame.dispatcher.handleEvents();
  }
}

