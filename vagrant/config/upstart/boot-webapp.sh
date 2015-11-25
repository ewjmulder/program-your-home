#!/bin/bash

###################################
# Program Your Home - Boot webapp #
###################################

# TODO: proper path
cd /home/pyh/program-your-home/web-app

mvn -l web-app.log exec:java -Dexec.mainClass=com.programyourhome.webapp.TestStandalone -Dexec.args="192.168.2.37 80" -Dexec.classpathScope=test
