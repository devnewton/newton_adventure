#!/bin/bash
mvn package -Pwin,deb,rpm
mkdir -p target/release
cp packages/generic/target/newton-adventure-installer*.jar target/release/
cp packages/win/target/newton-adventure-installer*.exe target/release/
cp packages/deb/target/newton-adventure*.deb target/release/
cp packages/rpm/target/rpm/newton-adventure/RPMS/*/newton-adventure-*.rpm target/release/
cp mods/bald/target/newton-adventure-mod-bald*-installer.jar target/release/
cp mods/duchatelet/target/newton-adventure-mod-duchatelet*-installer.jar target/release/
cp mods/retro/target/newton-adventure-mod-retro*-installer.jar target/release/
