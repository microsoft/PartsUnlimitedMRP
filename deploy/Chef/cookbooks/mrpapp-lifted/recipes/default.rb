#
# Cookbook Name:: mrpapp
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
script "install-mrp-app" do
 interpreter "bash"

code <<-EOH
 add-apt-repository ppa:openjdk-r/ppa -y
 apt-get update
 apt-get install openjdk-8-jdk -y
 apt-get install openjdk-8-jre -y
 apt-get install mongodb -y
 apt-get install tomcat7 -y
 apt-get install wget -y

 export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
 export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin

 wget https://oguzphackfest.blob.core.windows.net/scripts/MongoRecords.js

 sleep 10

 mongo ordering MongoRecords.js

 sed -i s/8080/9080/g /etc/tomcat7/server.xml

 wget https://oguzphackfest.blob.core.windows.net/scripts/mrp.war

 cp mrp.war /var/lib/tomcat7/webapps

 /etc/init.d/tomcat7 restart

 wget https://oguzphackfest.blob.core.windows.net/scripts/ordering-service-0.1.0.jar

 sleep 10

 /usr/lib/jvm/java-8-openjdk-amd64/bin/java -jar ordering-service-0.1.0.jar &

 echo "MRP application successfully deployed. Go to http://$HOSTNAME.cloudapp.net:9080/mrp"
EOH
end