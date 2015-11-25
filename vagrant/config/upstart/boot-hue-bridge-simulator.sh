#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

# TODO: proper path
cd /home/pyh/hue-bridge-simulator/server

# TODO: find better location
mvn exec:java -Dsimulator.properties.location="/home/pyh/simulator.properties"
