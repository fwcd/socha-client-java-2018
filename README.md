# SC18-JavaClient

My contribution to the [Software Challenge 2018](http://www.software-challenge.de) - an AI for a board game called "Hase und Igel". It uses a combination of various machine learning strategies to determine it's next move in the game.

![Architecture](https://github.com/fwcd/SC18-JavaClient/blob/master/architecture.jpg?raw=true)

## General notes

Package structure:

* **src/main/java** - Client source code
    * **com.fwcd.sc18** - Contains game ai logic
    * **sc.player2018** - Necessary client code to interact with the game and a very simple demo logic
* **src/test/java** - Testing source code
    * **com.fwcd.sc18.test**

The respective package-info.java files contain further information for each subpackage.

## Building and running the client

* [Download the Server here](http://www.software-challenge.de/downloads/).

* Build the client using Gradle: `./gradlew build` or `gradlew build` (depending on your shell)

* You should find the compiled JAR under the path target/runnableclient.jar

* Launch the Server-GUI and load the compiled client as a computer-player.
  (You can choose the opponent yourself... there is a [simple client](http://www.software-challenge.de/downloads/)
  which stupidly commits random legal moves while playing and thus is useful for testing.)