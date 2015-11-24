#!/bin/bash

###################################
# Program Your Home - Boot webapp #
###################################

# TODO: proper path
cd /home/vagrant/program-your-home/webapp

mvn -l web-app.log exec:java -Dexec.mainClass=com.programyourhome.webapp.TestStandalone -Dexec.args=\"0.0.0.0 80\" -Dexec.classpathScope=test
