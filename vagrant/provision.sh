#!/bin/bash

###################################################
# Program Your Home - Infrastructure installation #
###################################################
# Note: although not needed here, sudo is added anyway
# for ease of use of copy/pasting these commands to the terminal
# TODO: User/environment specifics should be configurable!
# TODO: Create and use a 'pyh' user throughout the PYH git cloning/building so it's not all under root.

echo "Starting Program Your Home provisioning script"

LOG_FILE="/home/vagrant/provisioning_"$(date "+%Y-%m-%d_%H-%M-%S.log")

echo "Creating log file for this provisioning run:" $LOG_FILE
sudo touch $LOG_FILE

TIMEZONE=$(< /vagrant/config/os/timezone)
echo "Setting timezone to "$TIMEZONE
sudo timedatectl set-timezone $TIMEZONE >> $LOG_FILE 2>&1

echo "Updating package list"
sudo apt-get update >> $LOG_FILE 2>&1

echo "Installing git"
sudo apt-get install --yes git >> $LOG_FILE 2>&1

echo "Installing maven"
sudo apt-get install --yes maven >> $LOG_FILE 2>&1

echo "Installing Java 8" # Oracle edition (note: installing after maven, so it will upgrade to this Java version as well)
sudo add-apt-repository ppa:webupd8team/java >> $LOG_FILE 2>&1
sudo apt-get update >> $LOG_FILE 2>&1
# Needs auto-answers to accept the licence.
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
sudo apt-get install --yes oracle-java8-installer >> $LOG_FILE 2>&1

echo "Installing IguanaIR & LIRC"
# The following commands are taken from the Iguanaworks wiki pages on how to install on Ubuntu:
# http://www.iguanaworks.net/wiki/doku.php?id=usbir:gettingstarted
# http://www.iguanaworks.net/wiki/doku.php?id=usbir:compilelirc#compiling_under_ubuntu_deb
# Note: IguanaIR transceiver must be connected during boot time or otherwise iguanaIR deamon must be restarted (at least in VM environment)
echo "deb http://iguanaworks.net/downloads/debian binary-amd64/" | sudo tee /etc/apt/sources.list.d/iguanaworks.list >> $LOG_FILE 2>&1
sudo apt-get update >> $LOG_FILE 2>&1
# Needs auto-answers for selecting remote and transmitter. We select None for both, since we'll override the hardware.conf anyway.
echo lirc lirc/remote select None | sudo /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
echo lirc lirc/transmitter select None | sudo /usr/bin/debconf-set-selections >> $LOG_FILE 2>&1
# Use --force-yes to install this unauthenticated package
sudo apt-get install --yes --force-yes iguanair >> $LOG_FILE 2>&1

echo "Adding 'iguanaIR' driver support to LIRC"
sudo apt-get build-dep --yes lirc >> $LOG_FILE 2>&1
sudo apt-get -b source lirc >> $LOG_FILE 2>&1
sudo dpkg -i lirc_0.*.deb >> $LOG_FILE 2>&1
# After much tinkering, trial & error it turns out we only need to set the remote and transmitter driver for LIRC to work correctly.
sudo cp /vagrant/config/lirc/hardware.conf /etc/lirc/hardware.conf >> $LOG_FILE 2>&1

echo "Starting iguanaworks and lirc services. Please note: IR will only work after a reboot!"
sudo /etc/init.d/iguanaIR start >> $LOG_FILE 2>&1
sudo /etc/init.d/lirc start >> $LOG_FILE 2>&1

echo "Installing & configuring PostgreSQL"
sudo apt-get install --yes postgresql postgresql-contrib >> $LOG_FILE 2>&1
# Listen on all interfaces, so the database is reachable from the outside.
sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/9.3/main/postgresql.conf >> $LOG_FILE 2>&1
# Make it possible to connect to postgresql from within the local network.
sudo sed -i "s/host    all             all             ::1\/128                 md5/host    all             all             192.168.2.0\/8            md5/g" /etc/postgresql/9.3/main/pg_hba.conf >> $LOG_FILE 2>&1
# Create a pyh user and set it's password. TODO: should be dynamic based on pyh properties.
sudo -u postgres psql -U postgres -d postgres -c "CREATE USER pyh WITH PASSWORD 'pyh';" >> $LOG_FILE 2>&1
# Create a pyh database.
sudo -u postgres psql -U postgres -c "CREATE DATABASE pyh OWNER pyh" >> $LOG_FILE 2>&1
# Create a schema for the shopping module.
sudo -u postgres psql -U postgres -d pyh -c "CREATE SCHEMA shopping AUTHORIZATION pyh" >> $LOG_FILE 2>&1
# Restart the postgres service for the changes in security to take effect.
sudo /etc/init.d/postgresql restart >> $LOG_FILE 2>&1

echo "Installing Event Store"
# Add the Event Store apt key to our trusted keys.
curl --silent --show-error https://apt-oss.geteventstore.com/eventstore.key | sudo apt-key add - >> $LOG_FILE 2>&1
# Add the deb archive to the system.
echo "deb [arch=amd64] https://apt-oss.geteventstore.com/ubuntu/ trusty main" | sudo tee /etc/apt/sources.list.d/eventstore.list >> $LOG_FILE 2>&1
# Update to get all new packages.
sudo apt-get update >> $LOG_FILE 2>&1
# Install the Event Store.
sudo apt-get install --yes eventstore-oss >> $LOG_FILE 2>&1
# Write the correct config file. The db and log paths are default, but set here explicitly for clarity.
sudo cp /vagrant/config/eventstore/eventstore.conf /etc/eventstore/eventstore.conf >> $LOG_FILE 2>&1
# Start the event store.
sudo service eventstore start >> $LOG_FILE 2>&1
# Wait a while to let the Event Store service boot up.
sleep 5
# Create the product stock projection.
curl --silent --show-error --request POST --user admin:changeit --data @/vagrant/config/eventstore/product-stock.js http://localhost:2113/projections/continuous?name=product-stock\&enabled=yes\&checkpoints=yes\&emit=no >> $LOG_FILE 2>&1

echo "Creating new user 'pyh'"
sudo adduser --disabled-password --shell /bin/bash --gecos "" pyh >> $LOG_FILE 2>&1

echo "Cloning hue-brigde-smulator from the Github repo and building"
cd /home/pyh
sudo -u pyh git clone https://github.com/ewjmulder/hue-bridge-simulator.git >> $LOG_FILE 2>&1
cd hue-bridge-simulator >> $LOG_FILE 2>&1
# Build the entire hue-bridge-simulator project.
sudo -u pyh mvn clean install >> $LOG_FILE 2>&1
# Copy the properties file. TODO: find better location
sudo -u pyh cp /vagrant/config/hue-bridge-simulator/simulator.properties /home/pyh/

echo "Cloning program-your-home from the Github repo and building"
cd /home/pyh
sudo -u pyh git clone https://github.com/ewjmulder/program-your-home.git >> $LOG_FILE 2>&1
cd program-your-home >> $LOG_FILE 2>&1
# Checkout the release branch
sudo -u pyh git checkout release >> $LOG_FILE 2>&1
# Install 3rd party libraries in local maven repo
cd philips-hue/lib >> $LOG_FILE 2>&1
sudo -u pyh bash install-jars-in-maven-repo.sh >> $LOG_FILE 2>&1
cd ../../voice-control/lib >> $LOG_FILE 2>&1
sudo -u pyh bash install-jars-in-maven-repo.sh >> $LOG_FILE 2>&1
cd ../.. >> $LOG_FILE 2>&1
# Build the entire program-your-home project
sudo -u pyh mvn clean install >> $LOG_FILE 2>&1

echo "Setting up Program Your Home applications as services and starting these services"
sudo cp /vagrant/config/upstart/pyh-server.conf /etc/init/ >> $LOG_FILE 2>&1
sudo cp /vagrant/config/upstart/pyh-hbs.conf /etc/init/ >> $LOG_FILE 2>&1
sudo cp /vagrant/config/upstart/pyh-webapp.conf /etc/init/ >> $LOG_FILE 2>&1
# Both pyh-hbs and pyh-webapp will start because they trigger on pyh-server's start.
sudo start pyh-server >> $LOG_FILE 2>&1

# Change ownership to vagrant of everything we've done in the vagrant home directory.
sudo chown -R vagrant:vagrant /home/vagrant >> $LOG_FILE 2>&1

echo "Finished Program Your Home provisioning script."
echo "If you experience any problems with the setup, please check the logfile ("$LOG_FILE") for any error messages."

echo "Please restart the VM with the IguanaWorks USB dongle plugged in the host machine to enable IR support"
