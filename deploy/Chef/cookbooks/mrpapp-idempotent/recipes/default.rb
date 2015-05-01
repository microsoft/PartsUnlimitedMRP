#
# Cookbook Name:: mrpapp-2
# Recipe:: default
#
# Copyright (c) 2015 The Authors, All Rights Reserved.

# Runs apt-get update
include_recipe "apt"

# Add the Open JDK apt repo
apt_repository 'openJDK' do
    uri 'ppa:openjdk-r/ppa'
    distribution 'trusty'
end

# Install JDK and JRE
apt_package 'openjdk-8-jdk' do
    action :install
end

apt_package 'openjdk-8-jre' do
    action :install
end

# Set Java environment variables
ENV['JAVA_HOME'] = "/usr/lib/jvm/java-8-openjdk-amd64"
ENV['PATH'] = "#{ENV['PATH']}:/usr/lib/jvm/java-8-openjdk-amd64/bin"

# Install MongoDB
apt_package 'mongodb' do
    action :install
end

# Install Tomcat 7
apt_package 'tomcat7' do
    action :install
end

# Load MongoDB data 
remote_file 'mongodb_data' do
    source 'https://oguzphackfest.blob.core.windows.net/scripts/MongoRecords.js'
    path './MongoRecords.js'
    action :create
    notifies :run, "script[mongodb_import]", :immediately
end

script 'mongodb_import' do
    interpreter "bash"
    action :nothing
    code "mongo ordering MongoRecords.js"
end

# Set tomcat port 
script 'tomcat_port' do 
    interpreter "bash"
    code "sed -i 's/Connector port=\".*\" protocol=\"HTTP\\/1.1\"$/Connector port=\"#{node['tomcat']['mrp_port']}\" protocol=\"HTTP\\/1.1\"/g' /etc/tomcat7/server.xml"
    not_if "grep 'Connector port=\"#{node['tomcat']['mrp_port']}\" protocol=\"HTTP/1.1\"$' /etc/tomcat7/server.xml"
    notifies :restart, "service[tomcat7]", :immediately
end

# Install the MRP app, restart the Tomcat service if necessary
remote_file 'mrp_app' do
    source 'https://oguzphackfest.blob.core.windows.net/scripts/mrp.war'
    path '/var/lib/tomcat7/webapps/mrp.war'
    action :create
    notifies :restart, "service[tomcat7]", :immediately
end

# Ensure Tomcat is running
service 'tomcat7' do
    action :start
end

remote_file 'ordering_service' do
    source 'https://oguzphackfest.blob.core.windows.net/scripts/ordering-service-0.1.0.jar'
    path './ordering-service-0.1.0.jar'
    action :create
    notifies :run, "script[stop_ordering_service]", :immediately
end

# Kill the ordering service
script 'stop_ordering_service' do
    interpreter "bash"
    # Only run when notifed
    action :nothing
    code "pkill -f ordering-service"
    only_if "pgrep -f ordering-service"
end

# Start the ordering service. 
script 'start_ordering_service' do
    interpreter "bash"
    code "/usr/lib/jvm/java-8-openjdk-amd64/bin/java -jar ordering-service-0.1.0.jar &"
    not_if "pgrep -f ordering-service"
end
