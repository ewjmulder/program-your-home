#!/bin/bash

#####################################
# Program Your Home - Boot service  #
#####################################

cd /home/pyh/program-your-home
# Perform an update. Since we're on the release branch, this should always result in a stable working version.
git pull
# Rebuild the project.
mvn clean install

cd bootstrap
# Start the server
mvn exec:exec -Dpyh.properties.location=../server/src/main/resources/com/programyourhome/config/server/properties/pyh.example.prod.properties
