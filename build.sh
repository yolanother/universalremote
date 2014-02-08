#!/bin/bash

pushd IrLib
ant releasea
popd

pushd sdk
ant deploy
popd

ant deploy
