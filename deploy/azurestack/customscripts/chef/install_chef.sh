#!/bin/bash

# Validate input parameters
if [[ !("$#" -eq 3) ]]; 
    then echo "Parameters missing for Chef Server 12 configuration." >&2
    exit 1
fi

# Get parameters
chef_fqdn=$1
chef_user=$2
chef_password=$3

sudo hostname ${chef_fqdn}

# Download & Install Chef Server 12
cd ~
wget https://packages.chef.io/files/stable/chef-server/12.15.7/ubuntu/16.04/chef-server-core_12.15.7-1_amd64.deb
sudo dpkg -i chef-server-core_*.deb

# Start Configuration
sudo chef-server-ctl reconfigure

# Configure user and organization
sleep 5

# Create user
sudo chef-server-ctl user-create ${chef_user} ${chef_user} LabUser ${chef_user}@partsunlimited.local ${chef_password} --filename /home/${chef_user}/${chef_user}.pem

# Create organization
sudo chef-server-ctl org-create partsunlimited 'Parts Unlimited, Inc.' --association_user ${chef_user} --filename /home/${chef_user}/partsunlimited-validator.pem

# Add the management GUI
sudo chef-server-ctl install chef-manage
sudo chef-server-ctl reconfigure
sudo chef-manage-ctl reconfigure --accept-license

#Add reporting
sudo chef-server-ctl install opscode-reporting
sudo chef-server-ctl reconfigure
sudo opscode-reporting-ctl reconfigure --accept-license
