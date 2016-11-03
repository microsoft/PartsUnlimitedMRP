#!/bin/bash

# Install PartsUnlimitedMRP dependencies
sudo apt-get update
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
sudo apt-get install mongodb -y
sudo apt-get install tomcat7 -y
sudo apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin

# Installing OMS
sudo wget https://github.com/Microsoft/OMS-Agent-for-Linux/releases/download/OMSAgent_Ignite2016_v1.2.0-75/omsagent-1.2.0-75.universal.x64.sh
sudo sha256sum ./omsagent-1.2.0-75.universal.x64.sh
sudo sh ./omsagent-1.2.0-75.universal.x64.sh --upgrade -w $1 -s $2

# Create deployment directory
sudo mkdir -p /var/lib/partsunlimited

# Kill java to stop current website
sudo pkill -9 'java'

# Remove old artifacts
sudo rm -f /var/lib/partsunlimited/*

# Copy files from deployment package
sudo find ../ -iname '*.?ar' -exec cp -t /var/lib/partsunlimited {} +;
sudo find . -iname 'MongoRecords.js' -exec cp -t /var/lib/partsunlimited {} +;
sudo find . -iname '*.sh' -exec cp -t /var/lib/partsunlimited {} +;

sudo mv /var/lib/partsunlimited/ordering-service-manager.sh /etc/init.d
sudo update-rc.d ordering-service-manager.sh defaults 

# Giving permissions to all newly downloaded files.
sudo chmod 754 /var/lib/partsunlimited/*

# Add the records to ordering database on MongoDB
sudo mongo ordering /var/lib/partsunlimited/MongoRecords.js

# Change Tomcat listening port from 8080 to 9080
sudo sed -i s/8080/9080/g /etc/tomcat7/server.xml

# Remove existing MRP directory and copy WAR file to Tomcat directory for auto-deployment
sudo rm -rf /var/lib/tomcat7/webapps/mrp
sudo cp /var/lib/partsunlimited/mrp.war /var/lib/tomcat7/webapps

# Restart Tomcat
sudo /etc/init.d/tomcat7 restart

# Run Ordering Service app
java -jar /var/lib/partsunlimited/ordering-service-0.1.0.jar &>/dev/null &

echo "MRP application successfully deployed. Go to http://<YourDNSname>:80/mrp"