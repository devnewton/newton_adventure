Requirements
============

Java JDK, [openjdk](http://openjdk.java.net/) or [Oracle](http://java.com): 1.7+
[Maven](http://www.maven.org) 3+,
[bash](https://www.gnu.org/software/bash/) 4+

Build
=====

Go to the source folder and run the build.sh script using bash:

	bash build.sh

Build android apk
-----------------

1. Edit the following properties in game/playn/android/pom.xml:

- android.sdk.path
- android.version

2. Run maven build with android profile

    mvn package -Pandroid

Install
=======

The build.sh script generate installers for various OS in the target/release subfolder. Use them to install the game.

Run without install 
===================

Go to the source folder and run the play.sh script using bash:

        bash play.sh

Develop, debug
==============

Newton Adventure is maven based project, open it with Netbeans or Eclipse.

Contribute
==========

Please contact me:

- by mail: devnewton at bci.im
- using IRC: #GGP on freenode.net.

