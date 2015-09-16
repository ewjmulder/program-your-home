#!/bin/bash

###################################################
# Program Your Home - Infrastructure installation #
###################################################

echo "Starting Program Your Home provisioning script"

LOG_FILE="provisioning_"$(date "+%Y-%m-%d_%H-%M-%S.log")

echo "Creating log file for this provisioning run:" $LOG_FILE
touch $LOG_FILE
chown vagrant:vagrant $LOG_FILE

echo "Setting timezone to Europe/Amsterdam"
timedatectl set-timezone Europe/Amsterdam

echo "Updating package list"
apt-get update >> $LOG_FILE 2>&1

echo "Installing git"
apt-get install --yes git >> $LOG_FILE 2>&1

echo "Installing maven"
apt-get install --yes maven >> $LOG_FILE 2>&1

echo "Installing Java 8" # Oracle edition
add-apt-repository ppa:webupd8team/java >> $LOG_FILE 2>&1
apt-get update >> $LOG_FILE 2>&1
# Needs auto-answers to accept the licence.
echo debconf shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
echo debconf shared/accepted-oracle-license-v1-1 seen true | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
apt-get install --yes oracle-java8-installer >> $LOG_FILE 2>&1

#TODO:
#- auto-install 3rd party libs in maven repo
#- LIRC (vs WinLIRC)

echo "Finished Program Your Home provisioning script"
