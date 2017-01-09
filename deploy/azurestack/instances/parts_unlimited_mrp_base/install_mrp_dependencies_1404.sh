#!/bin/bash

# Install PartsUnlimitedMRP dependencies
apt-get update
apt-get upgrade -y
add-apt-repository ppa:openjdk-r/ppa -y
apt-get update
apt-get install openjdk-8-jdk -y
apt-get install openjdk-8-jre -y
apt-get install mongodb -y
apt-get install tomcat7 -y
apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
