#!/bin/bash

###################################################
# Program Your Home - Start the server            #
###################################################

echo "Starting Program Your Home boot script"

LOG_FILE="boot_"$(date "+%Y-%m-%d_%H-%M-%S.log")

echo "Creating log file for this boot run:" $LOG_FILE
touch $LOG_FILE
chown vagrant:vagrant $LOG_FILE

#TODO
#- Start LIRC
#- Start Spring boot PYH server
#- Start webserver for webapp

echo "Finished Program Your Home boot script"
echo "(some services might take a little more time to be fully booted and operational)"
