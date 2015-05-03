# Building the OrderService #
The following command is used to build the OderService WAR file.

## Clone the GitHub repository ###

Prior to building, you should have cloned the git repository with the following command (assuming you have git installed already).

### Installing git ###
The following provides examples for obtaining git for Windows and Ubuntu Linux.

#### Windows ####
By far the easiest way to get git for Windows is use the GitHub installer that provides the command line interface along with a Windows client that makes managing and working with git easier and visual.

Navigate to [http://windows.github.com](http://windows.github.com).  Click on the 'Download GitHub for Windows'

When done, there will be 2 GitHub shortcuts created.  Open the "Git Shell".  This will be a PowerShell session with git and [POSH-GIT](https://github.com/dahlbyk/posh-git) ready to go.

```
cd <Some Local Path Where Child Directories will be Git Repositories>

# clone the repository and replace the <http://.....> path with the URL of the git repository
git clone https://github.com/Microsoft/PartsUnlimitedMRP.git 

#switch to the path.
cd PartsUnlimitedMRP
```
After this you will be in the Root of the cloned repository ready to build.

#### Linux ####
Assuming you are using Ubuntu 14+, utilize the Debian package manager.

```
cd ~/
# update package info from Ubuntu
sudo apt-get update

# run the install for git
sudo apt-get install git

# clone the repository and replace the <http://.....> path with the URL of the git repository
git clone https://github.com/Microsoft/PartsUnlimitedMRP.git

# switch to the path
cd PartsUnlimitedMRP
```
After this you will be in the Root of the cloned repository ready to build.

## Building ##

The following commands are used to build the ```ordering-service-0.1.0.jar``` file from the sources located in the clone of the git repository.

change to the ```./src/Backend/OrderService``` directory.


### Windows ###
From a PowerShell or CMD shell, run the following command.

```
.\gradlew.bat build

```

### Linux ###

```
#make sure you have the JDK installed. assumes Ubuntu 14.04 LTS.
sudo apt-get install default-jdk
```

At this point exit and restart your bash session to pick up the new settings. Or run ```. .profile  ``` from your home ```~/```

```
# ensure the 'gradlew' is executable
chmod +x gradlew

# now build...
./gradlew build
```

The build will run for a bit and will also run some tests. When done, the output it placed in the ```./builds/libs``` directory.

This creates the ```ordering-service-0.1.0.jar``` in the ```./build/libs``` directory.

## Cleaning all builds ##

### Windows ###
Run ```removeBuild.bat``` to remove the ```./build``` and the ```./buildSrc/build``` directories.

### Linux ###

Run the following native command:
```
rm -rf ./build ./buildSrc/build
```

