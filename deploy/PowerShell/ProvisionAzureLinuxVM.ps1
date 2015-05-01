#----------------------------------------------------
# Current Command Path Location
#----------------------------------------------------
[string]$ScriptPath = Split-Path -Path $MyInvocation.MyCommand.Path;

#----------------------------------------------------
# MRP Application Binaries and Deployment Script Location
#----------------------------------------------------
[string]$DropStorageAccountName = "<<CONFIGURE>>"
[string]$DropContainerName = "drops"
[string]$AppInstallScript = "Install-MRP.sh"
#----------------------------------------------------

#----------------------------------------------------
# Azure Infrastructure Configuration Variables
#----------------------------------------------------
[string]$SubscriptionName = "<<CONFIGURE>>"
[string]$AzureLocation = "West US"
[string]$StorageAccountName = "<<CONFIGURE>>"
[string]$ContainerName = "vm-vhds"
[string]$CloudSvcName = "<<CONFIGURE>>"
[string]$NetworkConfigFile = "AzureNetworks.netcfg"
[string]$VirtualNetwork = "<<CONFIGURE>>"
[string]$VSubNet = "BackEndSubnet"
[string]$LocalAdmin = "mrp_admin"
[string]$LocalPass = "P2ssw0rd"
[string]$LinuxVM_IP = "172.16.2.10"
[string]$LinuxVM_Name = "MRP-Linux"
#----------------------------------------------------

#----------------------------------------------------
#Copy Deployment files to Azure Blob Storage
#----------------------------------------------------
<#
Set-AzureStorageBlobContent -Blob "ExecuteAppInstall.sh" -Container "$Global:DropContainerName" -File "$Global:ScriptPath\AppDeploy\ExecuteAppInstall.sh" -Force
Set-AzureStorageBlobContent -Blob "InstallApp.sh" -Container "$Global:DropContainerName" -File "$Global:ScriptPath\AppDeploy\InstallApp.sh" -Force
Set-AzureStorageBlobContent -Blob "MongoRecords.js" -Container "$Global:DropContainerName" -File "$Global:ScriptPath\AppDeploy\MongoRecords.js" -Force
Set-AzureStorageBlobContent -Blob "ordering-service-0.1.0.jar" -Container "$Global:DropContainerName" -File "$Global:ScriptPath\AppDeploy\ordering-service-0.1.0.jar" -Force
#>
#----------------------------------------------------


#----------------------------------------------------
# Get Azure Subscription
#----------------------------------------------------
Write-Output "Connect to Azure Subscription"
Get-AzurePublishSettingsFile
$azurePublishSettingsFileLocation = Read-Host "Enter Azure Publish Settings File Path"
Import-AzurePublishSettingsFile -PublishSettingsFile $azurePublishSettingsFileLocation
Select-AzureSubscription -SubscriptionName $Global:SubscriptionName
Set-AzureSubscription -SubscriptionName $Global:SubscriptionName
Write-Output "Azure Subscription $Global:SubscriptionName is now the default PowerShell Azure subscription"
#-----------------------------------------------------

#----------------------------------------------------
# Create Azure Storage Account
#----------------------------------------------------
New-AzureStorageAccount -StorageAccountName "$Global:StorageAccountName" -Location "$Global:AzureLocation" -Type "Standard_LRS" -Description "Azure MRP Linux VM Storage Account" -Verbose;
Write-Output -Verbose "Storage account '$Global:StorageAccountNamecreated' `n"
Set-AzureSubscription -SubscriptionName "$Global:SubscriptionName" -CurrentStorageAccount "$Global:StorageAccountName" -Verbose
Write-Output "$Global:StorageAccountName now set as the current storage account for the subscription $Global:SubscriptionName `n"
#----------------------------------------------------

#----------------------------------------------------
# Create Azure Storage Account Blob Container for VM VHDs
#----------------------------------------------------
New-AzureStorageContainer -Name $Global:ContainerName -Permission Off -Verbose
Write-Output -Verbose "Storage container $Global:ContainerName created `n"
#----------------------------------------------------


#----------------------------------------------------
#Create Azure Virtual Network
#----------------------------------------------------
Set-AzureVNetConfig -ConfigurationPath "$Global:ScriptPath\$Global:NetworkConfigFile" -Verbose
Write-Output -Verbose "Azure Virtual Network(s) have been created. `n"
#----------------------------------------------------

#-----------------------------------------------------
# Create Linux VM
#-----------------------------------------------------
Write-Output "Begin creation of Azure Linux VM"
$images = Get-AzureVMImage `
        | where { $_.ImageFamily -eq "Ubuntu Server 14.10" } `
        | Sort-Object -Descending -Property PublishedDate
$latestImage = $images[0].ImageName

$ExtensionLocation = (Get-AzureStorageAccount -StorageAccountName "$Global:DropStorageAccountName").Endpoints[0] + "$Global:DropContainerName/$Global:AppInstallScript"
$PublicConfiguration = '{"fileUris":["' + "$ExtensionLocation" + '"], "commandToExecute": "sh ' + $Global:AppInstallScript + '"}' 

$vm = New-AzureVMConfig -Name $Global:LinuxVM_Name -ImageName $latestImage -InstanceSize "Medium" -DiskLabel "$Global:LinuxVM_Name-OS"  -MediaLocation "https://$Global:StorageAccountName.blob.core.windows.net/$Global:ContainerName/$Global:LinuxVM_Name-OS_Disk.vhd"|
        Add-AzureProvisioningConfig -Linux -LinuxUser "$Global:LocalAdmin" -Password "$Global:LocalPass" -Verbose |
        Add-AzureEndpoint -Name "HTTP" -PublicPort 80 -LocalPort 8080 -Protocol "tcp" |
        Add-AzureEndpoint -Name "HTTP2" -PublicPort 81 -LocalPort 8081 -Protocol "tcp" |
        Set-AzureSubnet -SubnetNames "$Global:VSubNet" |
        Set-AzureStaticVNetIP -IPAddress "$LinuxVM_IP" |
        Set-AzureVMExtension -ExtensionName "CustomScriptForLinux" -Publisher "Microsoft.OSTCExtensions" -Version "1.2" -PublicConfiguration $PublicConfiguration
Add-AzureDataDisk -CreateNew -HostCaching "None" -DiskLabel "DataDisk-1" -DiskSizeInGB 500 -LUN "1" -MediaLocation "https://$Global:StorageAccountName.blob.core.windows.net/$Global:ContainerName/$Global:LinuxVM_Name-Data_Disk1.vhd" -VM $vm
Add-AzureDataDisk -CreateNew -HostCaching "None" -DiskLabel "DataDisk-2" -DiskSizeInGB 500 -LUN "2" -MediaLocation "https://$Global:StorageAccountName.blob.core.windows.net/$Global:ContainerName/$Global:LinuxVM_Name-Data_Disk2.vhd" -VM $vm
New-AzureVM -ServiceName "$Global:CloudSvcName" -Location "$Global:AzureLocation" -VNetName "$Global:VirtualNetwork" -VMs $vm -DeploymentName "$Global:LinuxVM_Name-Provision" -Verbose
#-----------------------------------------------------
