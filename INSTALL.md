Requirements
============

Java JDK, [openjdk](http://openjdk.java.net/) or [Oracle](http://java.com): 1.7+
[Maven](http://www.maven.org) 3+

Manual procedure
================

Compile
-------

Go to the source folder and run maven with the following command:

	mvn package

This will generate an executable jar installer in packages/generic/target subfolder named newton-adventure-installer-${project.version}.jar

Install
-------

Go to the source folder and launch the generic installer:

    java -jar packages/generic/target/newton-adventure-installer-${project.version}.jar

Create other packages and installers
------------------------------------

Using maven 3 plugins, rpm, deb and exe package/installer can be generated:

	mvn clean package -Pdeb,rpm,win
