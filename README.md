# phantomclient

My contribution to the [Software Challenge 2018](http://www.software-challenge.de) - an AI for a board game called "Hase und Igel".

## General notes

Package structure:

* **src/main/java** - Client source code
    * **com.fwcd.sc18** - Contains game ai logic
    * **sc.player2018** - Necessary client code to interact with the game and a very simple demo logic
* **src/test/java** - Testing source code
    * **com.fwcd.sc18.test**

The respective package-info.java files contain further information for each subpackage.

## Setting up Eclipse for development

* Clone this repository to your pc using git:

      git clone https://bitbucket.org/TheDroide/phantomclient.git

* Link the root repository folder to your workspace by opening
  it through "File" > "Open Projects from File System..."

## Running the client

* [Download the Server here](http://www.software-challenge.de/downloads/).
  
* Build the client using the "build.xml" ANT-file (though it should already show up as a pre-configured project builder in Eclipse)
  
* You should find the compiled JAR under the path target/phantomclient.jar
  
* Launch the Server-GUI and load the compiled client as a computer-player.
  (You can choose the opponent yourself... there is a [simple client](http://www.software-challenge.de/downloads/),
  which stupidly commits random legal moves while playing and thus is useful for testing.)