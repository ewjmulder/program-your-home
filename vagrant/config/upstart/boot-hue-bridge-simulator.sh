#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

# TODO: proper path
cd /home/vagrant/hue-bridge-simulator/server

# TODO: find better location
mvn exec:java -Dsimulator.properties.location="/home/vagrant/simulator.properties"
