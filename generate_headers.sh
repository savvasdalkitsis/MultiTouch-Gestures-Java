#!/bin/bash

### MAC OS X ###
DIR_OUT=mac/
CP="-classpath target/classes"
P="com.savvasdalkitsis.mac.gestures"

javah -o $DIR_OUT/MacOsGestures.h $CP ${P}.MacOsGestures


### WINDOWS AND LINUX NOT SUPPORTED YET ###


