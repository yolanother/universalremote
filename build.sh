#!/bin/bash

stty -echo

pushd IrLib
ant releasea
popd

pushd sdk
ant deploy
popd

ant deploy

stty echo
