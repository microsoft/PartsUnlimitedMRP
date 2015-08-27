#!/bin/bash

# Set globals here before running the script
# Important: StorageAccountName should be lowercase
PublishSettingsFileLocation=""
SubscriptionName=""
AzureLocation=""
StorageAccountName=""
ContainerName=""
CloudSvcName=""
VirtualNetwork=""
VSubNet=""
LocalAdmin=""
LocalPass=""
LinuxVMName=""

# Add Azure account
azure account import $PublishSettingsFileLocation

# Set the Azure account you want to use
azure account set "$SubscriptionName"

# Create Storage account
azure storage account create --type LRS --location "$AzureLocation" $StorageAccountName --label $StorageAccountName

# Create Storage container
StorageKey=$(azure storage account keys list --json $StorageAccountName | grep -Po '(?<="primaryKey": ")[^"]*')
azure storage container create --container $ContainerName --account-name $StorageAccountName --account-key $StorageKey


# Set storage account environment variables
export AZURE_STORAGE_ACCOUNT="$StorageAccountName"
export AZURE_STORAGE_ACCESS_KEY="$StorageKey"

# Set the ACL on the container folder
azure storage container set --container $ContainerName -p Container

# Create VNET
azure network vnet create --location "$AzureLocation" --address-space 172.16.0.0 --cidr 12 --subnet-name $VSubNet --subnet-start-ip 172.16.0.0 --subnet-cidr 24 $VirtualNetwork

# Choose Storage account
azure storage account set $StorageAccountName

# Create VM with defaults using Ubuntu 14.10 image. Use --help to get information about available parameters ex: azure vm create --help
azure vm create $LinuxVMName --location "$AzureLocation" --vm-size Medium --ssh 22 --virtual-network-name $VirtualNetwork --subnet-names $VSubNet b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-15_04-amd64-server-20150818-en-us-30GB $LocalAdmin $LocalPass

# Open port 9080 & 8080 on the VM for the client app running on Tomcat
azure vm endpoint create $LinuxVMName 9080 9080
azure vm endpoint create $LinuxVMName 8080 8080