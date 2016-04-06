#!/bin/bash

###########################################
# Program Your Home - Boot PC instructor  #
###########################################

cd /home/spark/program-your-home/
# Perform an update. Since we're on the release branch, this should always result in a stable working version.
git pull

# Rebuild all projects.
cd pc-instructor-api
mvn clean install
cd ../pc-instructor
mvn clean install
cd ../pc-instructor-server
mvn clean install

# Start the server
mvn exec:java -Dpcinstructor.properties.location=./src/test/resources/pcinstructor.properties.prod
