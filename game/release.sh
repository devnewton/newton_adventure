#!/bin/sh
newton_adventure_version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)'`
rm newton_adventure-${newton_adventure_version}-installer.exe
rm newton_adventure-${newton_adventure_version}-installer.jar
mvn clean package -Pizpack,rpm,rpm32,src,not_deb
cp target/newton_adventure_${newton_adventure_version}_sources.zip ../www/downloads/
cp target/newton_adventure-${newton_adventure_version}-installer.jar ../www/downloads/
cp target/rpm/newton_adventure/RPMS/i686/newton_adventure-${newton_adventure_version}-1.i686.rpm ../www/downloads/
cp target/newton_adventure-${newton_adventure_version}-installer.jar ./
mvn install -Pwin
cp newton_adventure-${newton_adventure_version}-installer.exe  ../www/downloads/
mvn package -Prpm,rpm64
cp target/rpm/newton_adventure/RPMS/x86_64/newton_adventure-${newton_adventure_version}-1.x86_64.rpm ../www/downloads/
mvn package -Pdeb,!not_deb
cp target/newton_adventure_${newton_adventure_version}.deb ../www/downloads/newton-adventure_${newton_adventure_version}.deb
