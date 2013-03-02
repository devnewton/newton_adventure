#!/bin/sh
rm newton_adventure-1.9-installer.exe
rm newton_adventure-1.9-installer.jar
mvn clean package -Pdeb,rpm,rpm32
cp target/newton_adventure-1.9-installer.jar ../www/downloads/
cp target/newton_adventure_1.9.deb ../www/downloads/
cp target/rpm/newton_adventure/RPMS/i686/newton_adventure-1.9-1.i686.rpm ../www/downloads/
cp target/newton_adventure-1.9-installer.jar ./
mvn install -Pwin
cp newton_adventure-1.9-installer.exe  ../www/downloads/
mvn package -Pdeb,rpm,rpm64
cp target/rpm/newton_adventure/RPMS/x86_64/newton_adventure-1.9-1.x86_64.rpm ../www/downloads/

 