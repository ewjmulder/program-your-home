#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

# TODO: proper path
cd /home/pyh/program-your-home/bootstrap

# Perform an update. Since we're on the release branch, this should always result in a stable working version.
git pull
# Start the server
mvn exec:exec -Dpyh.properties.location=../server/src/main/resources/com/programyourhome/config/server/properties/pyh.example.prod.properties
