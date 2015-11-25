#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

# TODO: proper path
cd /home/pyh/program-your-home/bootstrap

mvn exec:exec -Dpyh.properties.location=../server/src/main/resources/com/programyourhome/config/server/properties/pyh.example.prod.properties
