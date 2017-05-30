#!/bin/bash

APPID=ti.map
VERSION=2.4.6

#cd android; ant clean; rm -rf /dist/*;rm -rf build/*; ant ;unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Documents/APPC_WORKSPACE/FlicTest/; cd  ..

cd android/; ant clean; rm -rf /dist/*;rm -rf build/*; mkdir build; mkdir build/docs;ant -v;rm -rf  ~/Documents/APPC_WORKSPACE/Hafenradler/modules/android/ti.map/*; unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Documents/APPC_WORKSPACE/Hafenradler/; cd  ..

