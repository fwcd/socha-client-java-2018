# phantomclient

My contribution to the [Software Challenge 2018](http://www.software-challenge.de), which
deals with creating an AI for a board game called "Hase und Igel".

## General notes

Package structure:

* **src/main/java** - Client source code
    * **com.thedroide.sc18** - Contains game ai logic
    * **sc.player2018** - Necessary client code to interact with the game and a very simple demo logic
* **src/test/java** - Testing source code
    * **com.thedroide.sc18.test**
        * **clientsimulator** - A tester that can perform mass-tests of game strategies
        * **unittest** - JUnit test files

The respective package-info.java files contain further information for each subpackage.

## Setting up Eclipse for development

* Clone this repository to your pc using git:

      git clone https://bitbucket.org/TheDroide/phantomclient.git

* Link the root repository folder to your workspace by opening
  it through "File" > "Open Projects from File System..."
  
* Get m2eclipse if you haven't already

* Add a new Run Configuration
    * Select Maven Build
    * Enter in the base directory field: ${project_loc:phantomclient}
    * Enter in the goals field: clean assembly:assembly

## Running the client

* [Download the Server here](http://www.software-challenge.de/downloads/).
  
* Launch the previously created Run Configuration to create a Runnable JAR in the target directory
    * Alternatively you could also run from the console: mvn clean assembly:assembly
  
* You should find the compiled JAR under the path target/phantomclient-runnable.jar
  
* Launch the Server-GUI and load the compiled client as a computer-player.
  (You can choose the opponent yourself... there is a [simple client](http://www.software-challenge.de/downloads/),
  which stupidly commits random legal moves while playing and thus is useful for testing.)