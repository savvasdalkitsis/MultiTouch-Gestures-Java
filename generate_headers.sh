#!/bin/bash

### MAC OS X ###
DIR_OUT=mac/
CP="-classpath target/classes"
P="com.savvasdalkitsis.mac.gestures"

javah -o $DIR_OUT/MacOsGesturesNative.h $CP ${P}.MacOsGesturesNative


### WINDOWS AND LINUX NOT SUPPORTED YET ###


