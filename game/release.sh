#!/bin/sh
rm newton_adventure-1.10-installer.exe
rm newton_adventure-1.10-installer.jar
mvn clean package -Pizpack,deb,rpm,rpm32,src
cp target/newton_adventure-1.10-installer.jar ../www/downloads/
cp target/newton_adventure_1.10.deb ../www/downloads/
cp target/rpm/newton_adventure/RPMS/i686/newton_adventure-1.10-1.i686.rpm ../www/downloads/
cp target/newton_adventure-1.10-installer.jar ./
mvn install -Pwin
cp newton_adventure-1.10-installer.exe  ../www/downloads/
cp newton_adventure_1.10_sources.zip ../www/downloads/
mvn package -Pdeb,rpm,rpm64
cp target/rpm/newton_adventure/RPMS/x86_64/newton_adventure-1.10-1.x86_64.rpm ../www/downloads/
