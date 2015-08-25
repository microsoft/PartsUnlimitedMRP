# Update current packages
sudo apt-get update

# Install Git, Gradle, Java, and MongoDB
sudo apt-get install git
sudo apt-get install gradle
sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb

# Install Node and npm
curl --silent --location https://deb.nodesource.com/setup_0.12 | sudo bash -
sudo apt-get install nodejs -y
sudo apt-get install npm -y

# Set environment variables for Java
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin