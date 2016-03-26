#!/bin/bash

###########################################
# Program Your Home - Boot PC instructor  #
###########################################

# Rebuild all projects.
cd /home/pyh/program-your-home/pc-instructor-api
mvn clean install
cd ../pc-instructor
mvn clean install
cd ../pc-instructor-server
mvn clean install

# Start the server
mvn exec:java -Dpcinstructor.properties.location=./src/test/resources/pcinstructor.properties.prod
