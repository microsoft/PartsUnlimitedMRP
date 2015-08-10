#!/bin/bash

# Auto-accept license for Java
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

# Install packages
add-apt-repository ppa:webupd8team/java -y
apt-get update
apt-get install oracle-java8-installer -y
apt-get install mongodb -y
apt-get install tomcat7 -y
apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export PATH=$PATH:/usr/lib/jvm/java-8-oracle/bin

# Create symlink to Java for Tomcat
sudo ln -s /usr/lib/jvm/java-8-oracle /usr/lib/jvm/default-java

# Configure the storage account and container name here
DropStorageAccountName=""
DropContainerName="mrp-drops"
AzureResource="https://$DropStorageAccountName.blob.core.windows.net/$DropContainerName/"

# Download MongoRecords.js from Azure blob storage
wget ${AzureResource}MongoRecords.js

# Wait for 10 seconds to make sure previous step is completed
sleep 10

# Add the records to ordering database on MongoDB
mongo --nodb MongoRecords.js

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

# Download the Ordering Service jar from Azure storage
wget ${AzureResource}ordering-service-0.1.0.jar

# Wait for 20 seconds to make sure previous step is completed
sleep 20

# Run Ordering Service app
java -jar ordering-service-0.1.0.jar &

echo "MRP application successfully deployed"
