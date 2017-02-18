#!/bin/bash

APPID=ti.map
VERSION=2.4.0

#cd android; ant clean; rm -rf /dist/*;rm -rf build/*; ant ;unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Documents/APPC_WORKSPACE/FlicTest/; cd  ..

cd android; ant clean; rm -rf /dist/*;rm -rf build/*; ant ;unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Documents/APPC_WORKSPACE/Hafenradler/; cd  ..

