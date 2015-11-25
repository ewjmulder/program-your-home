#!/bin/bash

###################################
# Program Your Home - Boot webapp #
###################################

# TODO: proper path
cd /home/pyh/program-your-home/web-app

mvn exec:java -Dexec.mainClass=com.programyourhome.webapp.TestStandalone -Dexec.args="192.168.2.37 8080" -Dexec.classpathScope=test
