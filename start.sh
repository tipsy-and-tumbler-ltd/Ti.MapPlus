#!/bin/bash

APPID=ti.map
VERSION=3.1.2

cd android; ant clean; rm -rf /dist/*;rm -rf build/*; ant ;unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Documents/APPC_WORKSPACE/FlicTest/; cd  ..
echo android/dist/$APPID-android-$VERSION.zip;

