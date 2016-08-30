#!/bin/bash

# Validate input parameters
if [[ !("$#" -eq 5) ]]; 
    then echo "Parameters missing for vsts agent configuration." >&2
    exit 1
fi

# Get parameters
vsts_account_name=$1
vsts_personal_access_token=$2
vsts_agent_name=$3
vsts_agent_pool_name=$4
user_account=$5

# Set up variables
vsts_url=https://$vsts_account_name.visualstudio.com

# Install dotnet core
sudo sh -c 'echo "deb [arch=amd64] https://apt-mo.trafficmanager.net/repos/dotnet-release/ xenial main" > /etc/apt/sources.list.d/dotnetdev.list'
sudo apt-key adv --keyserver apt-mo.trafficmanager.net --recv-keys 417A0893
sudo apt-get update -y

sudo apt-get install dotnet-dev-1.0.0-preview2-003121 -y

# Set up variables
agent_url=https://github.com/Microsoft/vsts-agent/releases/download/v2.103.1/vsts-agent-ubuntu.16.04-x64-2.103.1.tar.gz
agent_tar=${agent_url##*/}
agent_folder=/opt/vstsagent

# Get installation package
cd /tmp ; wget -q ${agent_url}


# Unpack installation package
sudo mkdir ${agent_folder}
cd ${agent_folder}
sudo tar xzf /tmp/${agent_tar}


# Configure agent
echo "Running agent configuration"
sudo -u ${user_account} bash ${agent_folder}/config.sh configure --url $vsts_url --agent $vsts_agent_name --pool $vsts_agent_pool_name --nostart --acceptteeeula --auth PAT --token $vsts_personal_access_token --unattended

# Configure agent to run as a service
sudo bash ${agent_folder}/svc.sh install
sudo bash ${agent_folder}/svc.sh start


# Install PartsUnlimitedMRP dependencies
apt-get update
apt-get install openjdk-8-jdk -y
apt-get install openjdk-8-jre -y
apt-get install mongodb -y
apt-get install tomcat7 -y
apt-get install wget -y

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin