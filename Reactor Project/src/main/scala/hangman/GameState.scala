package hangman

case class GameState(word: String, guessCount: Int, guessChars: Set[Char]) {
  require(word != null && word.length > 0)
  require(guessCount >= 0)
  require(guessChars != null)

  def isGameOver: Boolean = {
    isGameLost || isGameWon
  }

  def isGameLost: Boolean = {
    guessCount <= 0
  }

  def isGameWon: Boolean = {
    word.forall(c => guessChars.contains(c))
  }

  def getMaskedWord: String = {
    word.map{ c =>
      guessChars.contains(c) match {
        case true => c
        case false => '-'
      }
    }
  }

  def makeGuess(guess: Char): GameState = {
    word.contains(guess) match {
      case true => GameState(word, guessCount, guessChars + guess)
      case false => GameState(word, guessCount - 1, guessChars + guess)
    }
  }

}
