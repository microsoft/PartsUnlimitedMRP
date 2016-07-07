#Build MRP App for Linux#
This document explains how to build and manually install the MRP Application

To be able to run through this walkthrough there is a dependency on Java 8 SDK and JRE8. These can be found at the link below:
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

There are steps below for Ubuntu and Debian package manager to install the JDK & JRE.

##Build the MRP Solution 
In order to build the MRP solution we will need to install some more dependencies. 
1.  To build the project, navigate to the OrderService folder with cmd/PowerShell console and initiate the build by calling the gradlew file with the parameter of the build.gradle file.

`PS> .\gradlew build`

Once run you can find the compiled jar file under the “build/libs” folder.

##Create a Linux VM 
Create a Linux VM in Azure and signin onto it.

## Install git client ##

On Ubuntu and the Debian package manager

```
sudo apt-get update
sudo apt-get install git
```

##Clone the repository##

From your home path or wherever you wish to store the bits:
```
git clone https://github.com/Microsoft/PartsUnlimitedMRP.git
```

##Install Dependencies
1.  With your `ssh` console now open we need to add the following dependencies:
 - Java 8 SDK
 - Java 8 JRE
 - MongoDB
2. Enter the following command to install the dependencies above. This is for Ubuntu 14.10+
```
sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb
```
For Ubuntu 14.04:
```
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb
```
3.  To setup Java type the following commands to set your environment variables.
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
```
4.  To check your Java 8 installation, type “java -version”
NOTE: if you are still referencing an older version of Java that was installed as part of your machine you can update this using the following command:

`sudo update-alternatives --config java`
You will be presented with a list of Java installations. Type the number you wish to set as your java default.

##Setup MONGODB
1.  Open the mongo command line tools by typing the following command:
`/usr/bin/mongo`

2.  Select the ordering database to create it.
`> use ordering`

3. Add an object to the catalog collection
```
> x = {"skuNumber" : "ACC-001", "description" : "Shelving", "unit" : "meters", "unitPrice" : 10.5 }
> db.catalog.insert(x)
```
4.  Check the object was created 
`> db.catalog.find()`

you should see something like:
```
{ "_id" : ObjectId("5568a7aefa7a8f99400cbd1e"), "skuNumber" : "ACC-001", "description" : "Shelving", "unit" : "meters", "unitPrice" : 10.5 }

```

5. Now there is data in the database you can check it with the following command 
`> show dbs`
Which should show something similar to:
```
local   0.078125GB
ordering        0.203125GB
```

6.  Copy the commands from Appendix A to insert sample data into the database 


##Build Backend - `OrderService`##

1. Move from your home directory into the `src` directory (this assumes you used defaults during `git clone`) 
```
cd ~/PartsUnlimitedMRP/src/Backend/OrderService
```
2. Now, run the `./gradlew` shell script 
```
chmod +x gradlew
./gradlew build
```

This will pull down [gradle](http://gradle.org/) components then build the solution...

At this point you should see a message from `gradle` indicating success:
```
BUILD SUCCESSFUL
Total time: 3 mins 22.654 secs
```
Build output is located in:
```
cd ~/PartsUnlimitedMRP/src/Backend/OrderService/build/libs/
```
A single `JAR` file `ordering-service-0.1.0.jar`

##Build Backend - `IntegrationService`##
1. Move from your home directory into the `src` directory (this assumes you used defaults during `git clone`) 
```
cd ~/PartsUnlimitedMRP/src/Backend/IntegrationService
```
2. Now, run the `./gradlew` shell script
```
chmod +x gradlew
./gradlew build
```

This will pull down [gradle](http://gradle.org/) components and then build the solution...

At this point you should see a message from `gradle` indicating success:
```
BUILD SUCCESSFUL

Total time: 45.762 secs
```
Build output is located in:
```
cd ~/PartsUnlimitedMRP/src/Backend/IntegrationService/build/libs/
```
A single `JAR` file `integration-service-0.1.0.jar`



## Build Frontend - `Clients`##
The Web Front end is a static site that is built into a single WAR file.
1. Move from your home directory into the `src` directory (this assumes you used defaults during `git clone`) 
```
cd ~/PartsUnlimitedMRP/src/Clients
```
2. Now, run the `./gradlew` shell script
```
chmod +x gradlew
./gradlew build
```
This will pull down [gradle](http://gradle.org/) components and then build the solution...

At this point you should see a message from `gradle` indicating success:
```
BUILD SUCCESSFUL

Total time: 2..456 secs
```
Build output is located in:
```
cd ~/PartsUnlimitedMRP/src/Clients/build/libs/
```
A single `WAR` file `mrp.war`
