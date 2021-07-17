#!/bin/sh
echo "Create target directory"
mkdir -p target
cp -R Newton_Adventure.AppDir target/Newton_Adventure.AppDir
cd target

echo "Download bundable opendjk"
wget -c https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.11%2B9/OpenJDK11U-jdk_x64_linux_hotspot_11.0.11_9.tar.gz
tar xf OpenJDK11U-jdk_x64_linux_hotspot_11.0.11_9.tar.gz

echo "Copy Newton Adventure jar"
cp ../../../game/lwjgl/target/newton-adventure.jar Newton_Adventure.AppDir/

echo "Build custom jdk"
NEWTON_ADVENTURE_DEPS=`./jdk-11.0.11+9/bin/jdeps --print-module-deps Newton_Adventure.AppDir/newton-adventure.jar`
./jdk-11.0.11+9/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules $NEWTON_ADVENTURE_DEPS --output Newton_Adventure.AppDir/usr

wget -c https://github.com/AppImage/AppImageKit/releases/download/12/appimagetool-x86_64.AppImage
chmod +x appimagetool-x86_64.AppImage
ARCH=x86_64 ./appimagetool-x86_64.AppImage Newton_Adventure.AppDir/
