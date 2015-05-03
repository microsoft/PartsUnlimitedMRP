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

MRPInstallScriptName=""
DropStorageAccountName=""
DropContainerName=""
ScriptLocation="https://$DropStorageAccountName.blob.core.windows.net/$DropContainerName/$MRPInstallScriptName"

# Add Azure account
azure account import $PublishSettingsFileLocation

# Set the Azure account you want to use
azure account set $SubscriptionName

# Create Storage account
azure storage account create --location "$AzureLocation" $StorageAccountName --label $StorageAccountName 

# Create Storage container
StorageKey=$(azure storage account keys list $StorageAccountName | grep -Po '(Primary\s)\K[^\s]*')
azure storage container create --container $ContainerName --account-name $StorageAccountName --account-key $StorageKey

# Create VNET
azure network vnet create --location "$AzureLocation" --address-space 172.16.0.0 --cidr 12 --subnet-name $VSubNet --subnet-start-ip 172.16.0.0 --subnet-cidr 24 $VirtualNetwork

# Choose Storage account
azure storage account set $StorageAccountName

# Create VM with defaults using Ubuntu 14.10 image. Use --help to get information about available parameters ex: azure vm create --help
azure vm create $LinuxVMName --location "$AzureLocation" --vm-size Medium --ssh 22 --virtual-network-name $VirtualNetwork --subnet-names $VSubNet b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_10-amd64-server-20150202-en-us-30GB $LocalAdmin $LocalPass

# Add 2 data disks
azure vm disk attach-new $LinuxVMName 512 https://$StorageAccountName.blob.core.windows.net/$ContainerName/$LinuxVMName-data-1.vhd
azure vm disk attach-new $LinuxVMName 512 https://$StorageAccountName.blob.core.windows.net/$ContainerName/$LinuxVMName-data-2.vhd

# Open port 9080 on the VM for the client app running on Tomcat
azure vm endpoint create $LinuxVMName 9080 9080

# Use CustomScriptForLinux extension to deploy the app on the VM.
azure vm extension set $LinuxVMName CustomScriptForLinux Microsoft.OSTCExtensions 1.* -i '{"fileUris":["'$ScriptLocation'"], "commandToExecute": "sh '$MRPInstallScriptName'"}'