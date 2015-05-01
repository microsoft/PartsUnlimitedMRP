#!/bin/bash
$DropStorageAccountName = "<<CONFIGURE>>"
# Install packages
apt-get update
apt-get install openjdk-8-jdk -y
apt-get install openjdk-8-jre -y
apt-get install mongodb -y
apt-get install tomcat7 -y
apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin

$DropContainerName = "drops"
$AzureResource = "https://${DropStorageAccountName}.blob.core.windows.net/${DropContainerName}/"
# Download MongoRecords.js from Azure blog storage -- change oguzphackfest with the public container you have created
wget ${AzureResource}MongoRecords.js

# Wait for 10 seconds to make sure previous step is completed
sleep 10

# Add the records to ordering database on MongoDB
mongo ordering MongoRecords.js

# Change Tomcat listening port from 8080 to 9080
sed -i s/8080/9080/g /etc/tomcat7/server.xml

# Download the client WAR file
wget ${AzureResource}mrp.war

# Wait for 10 seconds to make sure previous step is completed
sleep 10

# Copy WAR file to Tomcat directory for auto-deployment
cp mrp.war /var/lib/tomcat7/webapps

# Restart Tomcat
/etc/init.d/tomcat7 restart

# Download the Ordering Service jar from Azure storage -- change oguzphackfest with the public container you have created
wget ${AzureResource}ordering-service-0.1.0.jar

# Wait for 20 seconds to make sure previous step is completed
sleep 20

# Run Ordering Service app
java -jar ordering-service-0.1.0.jar &

echo "MRP application successfully deployed. Go to http://$HOSTNAME.cloudapp.net:9080/mrp"
