# Building the Client #

The MRP client is a static website.  The build process here creates a single archive (war) file that is used by the Tomcat host for the site.

## Windows ##

Building on Windows the following command is used to build the client WAR file.

```
.\gradlew.bat build

```
This creates the ```mrp.war``` in the ```./build/libs``` directory.

## Linux ##

Building on Linux the following command is used to build the client WAR file.

```
# ensure the 'gradlew' is executable
chmod +x gradlew

# now build
./gradlew build

```

## Cleaning all builds ##

### Windows ###

Run ```removeBuild.bat``` to remove the ```./build``` directory.

### Linux ###

On Linux remove the ```./build``` with the following command to 'clean'

```
rm -rf ./build
```

## MRP.war file ##

Ensure you copy or move the ```./build/libs/mrp.war``` file to your location needed to run.

