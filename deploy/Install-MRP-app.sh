#!/bin/bash

# Auto-accept license for Java
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Install packages
add-apt-repository ppa:webupd8team/java
apt-get update
apt-get install oracle-java8-installer -y
apt-get install mongodb -y
apt-get install wget -y

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

# Download the Ordering Service jar from Azure storage
wget ${AzureResource}ordering-service-0.1.0.jar

# Wait for 20 seconds to make sure previous step is completed
sleep 20

# Run Ordering Service app
java -jar ordering-service-0.1.0.jar &

echo "MRP application successfully deployed"
