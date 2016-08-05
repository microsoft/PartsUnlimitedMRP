#!/bin/bash

# Validate input parameters
if [[ !("$#" -eq 3) ]]; 
    then echo "Parameters missing for chef server configuration." >&2
    exit 1
fi

# Get parameters
azure_fqdn=$1
chef_user=$2
chef_password=$3


while [ ! -f /var/log/cloud-init-output.log ] 
do sleep 2; done

tail -f /var/log/cloud-init-output.log | while read LOGLINE
do
   [[ "${LOGLINE}" == *"finished"* ]] && pkill -P $$ tail
done

echo "Initial Chef Server Configuration done"


## Finish Chef Server configuration

# Remove the Nginx configuration for the existing Chef Analytics configuration
sudo rm /var/opt/opscode/nginx/etc/nginx.d/analytics.conf

# Update /etc/chef-marketplace/marketplace.rb with the api_fqdn of the machine
echo api_fqdn \"${azure_fqdn}\" | sudo tee -a /etc/chef-marketplace/marketplace.rb

# Update /etc/opscode-analytics/opscode-analytics.rb with analytics_fqdn of the machine
echo analytics_fqdn \"${azure_fqdn}\" | sudo tee -a /etc/opscode-analytics/opscode-analytics.rb

# Update the hostname and reconfigure Chef
sudo chef-marketplace-ctl hostname ${azure_fqdn}

#Reconfigure Chef Analytics
sudo opscode-analytics-ctl reconfigure


## Configure user and organization
sleep 5
# Create user
sudo chef-server-ctl user-create ${chef_user} ${chef_user} LabUser ${chef_user}@partsunlimited.local ${chef_password} --filename /home/${chef_user}/${chef_user}.pem

# Create organization
sudo chef-server-ctl org-create partsunlimited 'Parts Unlimited, Inc.' --association_user ${chef_user} --filename /home/${chef_user}/partsunlimited-validator.pem