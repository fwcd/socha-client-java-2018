# sochaclient

My contribution to the annual [Software Challenge](http://www.software-challenge.de), which
deals with creating an AI for a board game called "Hase und Igel".

## General notes

Package structure:

* **src** - Client source code
    * **com.thedroide.sc18** - Contains my game ai logic
    * **sc.player2018** - Necessary client code to interact with the game and a very simple demo logic
* **test** - Testing source code
    * **com.thedroide.clientsimulator** - A tester that can perform mass-tests of game strategies

The respective package-info.java files contain further information for each subpackage.

## Setting up Eclipse for development

* Clone this repository to your pc using git:

      git clone https://bitbucket.org/TheDroide/sochaclient.git

* Link the root repository folder to your workspace by opening
  it through "File" > "Open Projects from File System..."

## Running the client

* [Download the Server here](http://www.software-challenge.de/downloads/).
  
* Compile the client using the included ANT-script "build.xml".
  (The compiled jar will be placed in the "builds" folder)
  
  Note that an automatic ANT-builder already should be preconfigured to compile
  when saving in Eclipse!
  
* Launch the Server-GUI and load the compiled client as a computer-player.
  (You can choose the opponent yourself... there is a [simple client](http://www.software-challenge.de/downloads/),
  which stupidly commits random legal moves while playing and thus is useful
  for testing.)