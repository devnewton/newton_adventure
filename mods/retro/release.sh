#!/bin/sh
newton_adventure_version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)'`
rm newton_adventure_mod_pack1-${newton_adventure_version}-installer.exe
rm newton_adventure_mod_pack1-${newton_adventure_version}-installer.jar
mvn clean package
cp target/newton_adventure_mod_pack1-${newton_adventure_version}-installer.jar ./
mvn install -Pwin