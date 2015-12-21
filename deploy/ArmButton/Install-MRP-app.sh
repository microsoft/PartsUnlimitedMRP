#!/bin/sh

# Kill java to stop current website
pkill -9 'java'

# Remove old artifacts
if [ ! -d /var/lib/partsunlimited ]; then
	mkdir /var/lib/partsunlimited
else
	rm -f /var/lib/partsunlimited/MongoRecords.js*
	rm -f /var/lib/partsunlimited/mrp.war*
	rm -f /var/lib/partsunlimited/ordering-service-0.1.0.jar*
fi

# Install packages
apt-get update
apt-get install openjdk-7-jdk -y
apt-get install openjdk-7-jre -y
apt-get install mongodb -y
apt-get install tomcat7 -y
apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-7-openjdk-amd64/bin

# Copy the mongoRecords.js to the correct location
cp ./MongoRecords.js /var/lib/partsunlimited/

# Add the records to ordering database on MongoDB
mongo ordering /var/lib/partsunlimited/MongoRecords.js

# Change Tomcat listening port from 8080 to 9080
sed -i s/8080/9080/g /etc/tomcat7/server.xml

# Copy war to deployment dir
cp ./mrp.war /var/lib/tomcat7/webapps/

# Restart Tomcat
/etc/init.d/tomcat7 restart

# Copy Ordering Service jar to correct location
cp ./ordering-service-0.1.0.jar /var/lib/partsunlimited/

# Run Ordering Service app
java -jar /var/lib/partsunlimited/ordering-service-0.1.0.jar &

echo "MRP application successfully deployed. Go to http://host.location.cloudapp.net:8080/mrp"
