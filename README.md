# sochaclient

My contribution to the yearly [Software Challenge](http://www.software-challenge.de), which
deals with creating an AI for a board game called "Hase und Igel".

## Setting up Eclipse for development

* Simply link the main repository folder to your workspace by opening
  it through "File" > "Open Projects from File System..."

## Running the client

* [Download the Server here](http://www.software-challenge.de/downloads/).
  
* Compile the client using the included ANT-script "build.xml".
  (The compiled jar will be placed in the parent directory of your cloned repository)
  
  *Note that an automatic ANT-builder already should be preconfigured to compile
  when saving in Eclipse.*
  
* Launch the Server-GUI and load the compiled client as a computer-player.
  (You can choose the opponent yourself... there is a [simple client](http://www.software-challenge.de/downloads/),
  which stupidly commits random legal moves while playing and thus is useful
  for testing.)