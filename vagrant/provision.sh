#!/bin/bash

###################################################
# Program Your Home - Infrastructure installation #
###################################################

echo "Starting Program Your Home provisioning script"

LOG_FILE="provisioning_"$(date "+%Y-%m-%d_%H-%M-%S.log")

echo "Creating log file for this provisioning run:" $LOG_FILE
touch $LOG_FILE
chown vagrant:vagrant $LOG_FILE

# TODO: should be configurable!
echo "Setting timezone to Europe/Amsterdam"
timedatectl set-timezone Europe/Amsterdam

echo "Updating package list"
#apt-get update >> $LOG_FILE 2>&1

echo "Installing git"
#apt-get install --yes git >> $LOG_FILE 2>&1

echo "Installing maven"
#apt-get install --yes maven >> $LOG_FILE 2>&1

echo "Installing Java 8" # Oracle edition
#add-apt-repository ppa:webupd8team/java >> $LOG_FILE 2>&1
#apt-get update >> $LOG_FILE 2>&1
# Needs auto-answers to accept the licence.
echo debconf shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
echo debconf shared/accepted-oracle-license-v1-1 seen true | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
#apt-get install --yes oracle-java8-installer >> $LOG_FILE 2>&1

echo "Installing IguanaIR & LIRC"
# The following commands are taken from the Iguanaworks wiki pages on how to install on Ubuntu:
# http://www.iguanaworks.net/wiki/doku.php?id=usbir:gettingstarted
# http://www.iguanaworks.net/wiki/doku.php?id=usbir:compilelirc#compiling_under_ubuntu_deb
# Note: IguanaIR transceiver must be connected during boot time or otherwise iguanaIR deamon must be restarted (at least in VM environment)
echo "deb http://iguanaworks.net/downloads/debian binary-amd64/" | tee /etc/apt/sources.list.d/iguanaworks.list >> $LOG_FILE 2>&1
apt-get update >> $LOG_FILE 2>&1
# Needs auto-answers for selecting remote and transmitter. We select None for both, since we'll override the hardware.conf anyway.
echo lirc lirc/remote select None | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
echo lirc lirc/transmitter select None | /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
# Use --force-yes to install this unauthenticated package
#apt-get install --yes --force-yes iguanair >> $LOG_FILE 2>&1

echo "Adding 'iguanaIR' driver support to LIRC"
#apt-get build-dep --yes lirc >> $LOG_FILE 2>&1
#apt-get -b source lirc >> $LOG_FILE 2>&1
#dpkg -i lirc_0.*.deb >> $LOG_FILE 2>&1
# After much tinkering, trial & error it turns out we only need to set the remote and transmitter driver for LIRC to work correctly.
echo -e "REMOTE_DRIVER=\"iguanaIR\"\nTRANSMITTER_DRIVER=\"iguanaIR\"" | tee /etc/lirc/hardware.conf >> $LOG_FILE 2>&1

echo "Installing & configuring PostgreSQL"
apt-get install --yes postgresql postgresql-contrib >> $LOG_FILE 2>&1
# Listen on all interfaces, so the database is reachable from the outside.
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/9.3/main/postgresql.conf >> $LOG_FILE 2>&1
# Make it possible to connect to postgresql from within the local network.
sed -i "s/host    all             all             ::1\/128                 md5/host    all             all             192.168.2.0\/8            md5/g" /etc/postgresql/9.3/main/pg_hba.conf >> $LOG_FILE 2>&1
# Create a pyh user and set it's password. TODO: should be dynamic based on pyh properties.
sudo -u postgres psql -U postgres -d postgres -c "CREATE USER pyh WITH PASSWORD 'pyh';" >> $LOG_FILE 2>&1
# Create a pyh database.
sudo -u postgres psql -U postgres -c "CREATE DATABASE pyh OWNER pyh" >> $LOG_FILE 2>&1
# Create a schema for the shopping module.
sudo -u postgres psql -U postgres -d pyh -c "CREATE SCHEMA shopping AUTHORIZATION pyh" >> $LOG_FILE 2>&1
# Restart the postgres service for the changes in security to take effect.
/etc/init.d/postgresql restart >> $LOG_FILE 2>&1

#TODO:
#- Event Store - docs.geteventstore.com/server/3.1.0/installing-from-debian-repositories/
#- auto-install 3rd party libs in maven repo

echo "Finished Program Your Home provisioning script"

echo "Please restart the VM with the IguanaWorks USB dongle plugged in the host machine to enable IR support"
