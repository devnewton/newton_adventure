Newton Adventure is a free and open source 2D platform game.

The game can be run by double clicking on the newton_adventure-${project.version}.jar
file or using one of the run_* script.

It will run in 800x600 resolution
by default, but this can be configured with the options menu or in the 
~/.config/newton_adventure/config.properties file ( '~' is your home directory).

THE GAME WILL ONLY WORK IF:
 - YOU HAVE INSTALLED JAVA 1.6 OR HIGHER. (HEADLESS JAVA VERSION WONT WORK).
 - YOU HAVE AN OPENGL ENABLED COMPUTER. THAT MEANS A DECENT GRAPHIC CARD WITH
   DECENT DRIVER.
 - A SUPPORTED RESOLUTION BY YOUR GRAPHIC CARD AND YOUR SCREEN IS CHOSEN IN 
   THE ~/.config/newton_adventure/config.properties FILE.

If it does not work, you can try to get debug messages by running the game
like this:

java -jar -Dorg.lwjgl.util.Debug=true newton_adventure-${project.version}.jar

Go to http://devnewton.bci.im/ for news and updates.

