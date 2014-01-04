#!/usr/bin/env sh
if [ -e /usr/lib/jvm/java-7-openjdk-`/usr/share/jarwrapper/java-arch.sh`/bin/java ]
then
	/usr/lib/jvm/java-7-openjdk-`/usr/share/jarwrapper/java-arch.sh`/bin/java -jar /opt/newton-adventure/newton-adventure-lwjgl-${project.version}.jar $*
else
/opt/newton-adventure/newton-adventure-lwjgl-${project.version}.jar $*
fi
