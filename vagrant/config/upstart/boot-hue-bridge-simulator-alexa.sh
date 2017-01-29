#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

cd /home/pyh/hue-bridge-simulator/server

# TODO: find better location
mvn exec:java -Dsimulator.properties.location="/home/pyh/simulator.alexa.properties"
