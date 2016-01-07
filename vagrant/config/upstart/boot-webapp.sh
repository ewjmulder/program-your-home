#!/bin/bash

###################################
# Program Your Home - Boot webapp #
###################################

# TODO: proper path
cd /home/pyh/program-your-home/web-app
# Perform an update. Since we're on the release branch, this should always result in a stable working version.
git pull
# Rebuild the project.
mvn clean install

# Start the webapp
mvn exec:java -Dexec.mainClass=com.programyourhome.webapp.TestStandalone -Dexec.args="192.168.2.37 8080" -Dexec.classpathScope=test
