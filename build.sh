#!/bin/bash

stty -echo

pushd IrLib
ant release
popd

pushd sdk
ant deploy
popd

ant deploy

stty echo
