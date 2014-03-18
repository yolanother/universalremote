#!/bin/bash

stty -echo

rm -rf ../StandOut/library/bin/res/crunch


pushd IrLib
ant release
popd

pushd sdk
ant deploy
popd

ant deploy

cat version.properties
stty echo
