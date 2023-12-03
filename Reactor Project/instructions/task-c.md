# Task C - The Hangman Game

The Hangman application must consist of a server implementation based on the Reactor pattern. The server communicates through TCP with its clients and uses the Reactor from the previous task to handle all incoming network communication.

The server is given the word to be guessed on startup and the number of incorrect guesses needed to lose. The word consists of lower-case alphabetical characters, from a to z.

Initially, all letters in the word are shown to the players as dashes. The players guess one letter at a time. If the word to be guessed does not contain the guessed letter (irrespective of whether it has been guessed previously), the amount of remaining incorrect guesses is decremented; if it has reached zero, the game ends. Likewise, if the word to be guessed contains the guessed letter (irrespective of whether it has been guessed previously), the amount of remaining incorrect guesses remains unchanged. If all letters in the word have been guessed, the game also ends. Each time a player guesses a letter, the server sends each player who has joined the game a message describing the state of the guessing so far; this message contains the word to be guessed with all correctly guessed letters exposed. Players may connect and disconnect at any time.

Players share guesses. The server must terminate (closing all network connections) when the game ends, immediately after the message describing the last guess has been sent to all players.

> Tip: The essential game rules are already implemented in the class `hangman.GameState`.

## Game Protocol

The Hangman server must communicate with its clients using plain text (you may assume the clients use the server's character set) sent through TCP connections. The server must listen for incoming connections (until the game ends) on a port chosen at server startup. The port can be chosen at random (default), or you may implement logic to assign the port via an application argument (optional).

> Tip: `hangman.util.AcceptHandle` implements the necessary logic to use a random free port and print this port to `System.out`.

> Tip: Some operating systems may prevent you from repeatedly starting an application on the same port.

Each connection corresponds to a player; the player opens a connection to the server when he/she wants to join and may close it at any time. The first line sent by the client is a single line containing the player's name (one field, 1 or more alphabetic characters), and in response, it receives the following line, after which the player is considered to have joined the game:

```
<guessed word> <number of remaining tries>
```

Every time a guess is made, the server sends to all clients who have identified themselves a line of the form:

```
<guessed letter> <guessed word> <remaining tries> <name of last guesser>
```

The guessed word is the word to be guessed with unknown characters marked by dashes ("-"). Each response is a single line with 2 or 4 fields (as above) separated by (1 or more) spaces (leading/trailing spaces may exist). Lines are always terminated as in the server's native encoding. You may assume this encoding is a superset of ASCII and that all inputs are valid ASCII.

Clients send guesses (at any time) as single lines containing a single lower-case alphabetic character through the TCP connection (extraneous characters and newlines are not allowed; you may ignore them or report an error, for example).

## Usage 

The Hangman servers requires two startup arguments: the word to guess and the positive number of allowed failed guesses. The application must print the server port number to `System.out` so you know what TCP port clients must connect to.

> Example: `sbt "run concurrency 9"` from the command prompt or `run concurrency 9` inside the SBT shell.

Use terminal applications, such as `telnet` and `netcat`, to connect to the Hangman server as a client. 

> Tip: Windows 10 users can install a command line `telnet` client via: "Control panel" -> "Turn Windows features on or off" -> Enable "Telnet Client".

> Tip: The client might not interactively show you what you are typing into the client, but will still send the text once you hit the 'enter' key.

## Additional Requirements

Don't use any custom methods or classes of your Reactor implementation. You may only use the methods defined in the the Reactor specification. Your Hangman code must not depend on your Reactor code, and your Hangman code must work with any Reactor which implements the specification.

Your Hangman application must exit cleanly. Having to use `System.exit()` or a comparable method, that terminates the entire JVM, is a sign of implementation problems. Do not use daemon threads to circumvent this requirement.

Your Hangman must be a single threaded application. All of the concurrency and threading is already encapsulated inside the Reactor implementation. The Hangman server may only directly use one thread (the main thread); any other threads must be created and managed by the Reactor. Similarly, the Hangman server may not use any synchronisation mechanisms directly. There may not be any `synchronized` or `volatile` keywords inside the `hangman` package.

## Examples 

### Game 1

The game is initialized with the word `foobar` and a limit of 4 failed guesses.

I/O for the user Ted, with Ted's input preceded by the § symbol:

```
§ Ted
------ 4
§ a
a ----a- 4 Ted
§ b
b ---ba- 4 Ted
§ c
c ---ba- 3 Ted
§ d
d ---ba- 2 Ted
§ e
e ---ba- 1 Ted
§ f
f f--ba- 1 Ted
§ g
g f--ba- 0 Ted
```

### Game 2

The game is initialized with the word `concurrency` and a limit of ten failed guesses.

I/O for User Alice, with Alice's input preceded by the § symbol:

```
§ Alice
----------- 10
§ z
z ----------- 9 Alice
§ e
e -------e--- 9 Alice
o -o-----e--- 9 Bob
§ n
n -on----en-- 9 Alice
§ r
r -on--rren-- 9 Alice
y -on--rren-y 9 Bob
i -on--rren-y 8 Bob
c conc-rrency 8 Bob
§ u
u concurrency 8 Alice
```

I/O for user Bob, with Bob's input preceded by the § symbol:

```
§ Bob
-------e--- 9
§ o
o -o-----e--- 9 Bob
n -on----en-- 9 Alice
r -on--rren-- 9 Alice
§ y
y -on--rren-y 9 Bob
§ i
i -on--rren-y 8 Bob
§ c
c conc-rrency 8 Bob
u concurrency 8 Alice
```

## Submission

Submit your implementation as the file `HangmanGame.scala` through A+. If your implementation uses multiple classes, include all of them in the same file. All of your code must be in the `hangman` package.

The first line of your submission must be a comment line, with you student number, last name and first name.

> Example: //123456 Teekkari, Teemu

## Grading

| Points |  |
| ------ | ------ |
| 90 | Implemented the game correctly |
| 10 | Code comments helpful for understanding the implementation |
