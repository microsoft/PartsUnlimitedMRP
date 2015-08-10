Switch-AzureMode -Name AzureServiceManagement

#----------------------------------------------------
# Current Command Path Location
#----------------------------------------------------
[string]$ScriptPath = Split-Path -Path $MyInvocation.MyCommand.Path

#----------------------------------------------------
# MRP Application Binaries and Deployment Script Location
#----------------------------------------------------
[string]$StorageAccountName = "<<CONFIGURE>>"
[string]$DropContainerName = "mrp-drops"
[string]$AppInstallScript = "Install-MRP-app.sh"
#----------------------------------------------------

#----------------------------------------------------
# Azure Infrastructure Configuration Variables
#----------------------------------------------------
[string]$SubscriptionName = "<<CONFIGURE>>"
[string]$AzureLocation = "West US"
[string]$VhdsContainerName = "mrp-vhds"
[string]$CloudSvcName = "<<CONFIGURE>>"
[string]$NetworkConfigFile = "AzureNetworks.netcfg"
[string]$VirtualNetwork = "mrp-linux-vnet"
[string]$VSubNet = "BackEndSubnet"
[string]$LocalAdmin = "mrp_admin"
[string]$LocalPass = "P2ssw0rd"
[string]$LinuxVM_IP = "172.16.2.10"
[string]$LinuxVM_Name = "MRP-Linux"
#----------------------------------------------------

#----------------------------------------------------
# Get Azure Subscription
#----------------------------------------------------
Write-Output "Connect to Azure Subscription"
$sub = Get-AzureSubscription -Name $SubscriptionName
if ($sub -eq $null) {
    Get-AzurePublishSettingsFile
    $azurePublishSettingsFileLocation = Read-Host "Enter Azure Publish Settings File Path"
    Import-AzurePublishSettingsFile -PublishSettingsFile $azurePublishSettingsFileLocation
}

Select-AzureSubscription -SubscriptionName $SubscriptionName
Write-Output "Azure Subscription $SubscriptionName is now the default PowerShell Azure subscription"
#-----------------------------------------------------

#----------------------------------------------------
# Create Azure Storage Account (if missing)
#----------------------------------------------------
$storage = Get-AzureStorageAccount -StorageAccountName $StorageAccountName -ErrorAction Ignore
if ($storage -eq $null) {
    New-AzureStorageAccount -StorageAccountName $StorageAccountName -Location $AzureLocation -Type "Standard_LRS" -Description "Azure MRP Storage Account" -Verbose
    Write-Output -Verbose "Storage account '$StorageAccountName' created"
}

Set-AzureSubscription -SubscriptionName $SubscriptionName -CurrentStorageAccount $StorageAccountName -Verbose
Write-Output "$StorageAccountName now set as the current storage account for the subscription $SubscriptionName"
#----------------------------------------------------

#----------------------------------------------------
# Create Azure Storage Account Blob Container for VM VHDs
#----------------------------------------------------
$container = Get-AzureStorageContainer -Name $DropContainerName -ErrorAction Ignore
if ($container -eq $null) {
    New-AzureStorageContainer -Name $DropContainerName -Permission Blob -Verbose
    Write-Output -Verbose "Storage container $DropContainerName created"
}

$container = Get-AzureStorageContainer -Name $VhdsContainerName -ErrorAction Ignore
if ($container -eq $null) {
    New-AzureStorageContainer -Name $VhdsContainerName -Permission Off -Verbose
    Write-Output -Verbose "Storage container $VhdsContainerName created"
}
#----------------------------------------------------

#----------------------------------------------------
# Copy Deployment files to Azure Blob Storage
#----------------------------------------------------
$script = [IO.File]::ReadAllText("$ScriptPath\..\$AppInstallScript") -replace 'DropStorageAccountName=""', "DropStorageAccountName=`"$StorageAccountName`""
[IO.File]::WriteAllText("$ScriptPath\..\$AppInstallScript", $script, (New-Object System.Text.UTF8Encoding $false))
Set-AzureStorageBlobContent -Blob $AppInstallScript -Container $DropContainerName -File "$ScriptPath\..\$AppInstallScript" -Force
Set-AzureStorageBlobContent -Blob "MongoRecords.js" -Container $DropContainerName -File "$ScriptPath\..\MongoRecords.js" -Force
Set-AzureStorageBlobContent -Blob "ordering-service-0.1.0.jar" -Container $DropContainerName -File "$ScriptPath\..\..\builds\ordering-service-0.1.0.jar" -Force
Set-AzureStorageBlobContent -Blob "mrp.war" -Container $DropContainerName -File "$ScriptPath\..\..\builds\mrp.war" -Force
#----------------------------------------------------

#----------------------------------------------------
# Create Azure Virtual Network
#----------------------------------------------------
Set-AzureVNetConfig -ConfigurationPath "$ScriptPath\$NetworkConfigFile" -Verbose
Write-Output -Verbose "Azure Virtual Network(s) have been created"
#----------------------------------------------------

#-----------------------------------------------------
# Create Linux VM
#-----------------------------------------------------
Write-Output "Begin creation of Azure Linux VM"
$images = Get-AzureVMImage `
        | where { $_.ImageFamily -eq "Ubuntu Server 14.04 LTS" } `
        | Sort-Object -Descending -Property PublishedDate
$latestImage = $images[0].ImageName

$ExtensionLocation = (Get-AzureStorageAccount -StorageAccountName $StorageAccountName).Endpoints[0] + "$DropContainerName/$AppInstallScript"
$PublicConfiguration = '{"fileUris":["' + "$ExtensionLocation" + '"], "commandToExecute": "sh ' + $AppInstallScript + '"}' 

$vm = New-AzureVMConfig -Name $LinuxVM_Name -ImageName $latestImage -InstanceSize "Medium" -DiskLabel "$LinuxVM_Name-OS"  -MediaLocation "https://$StorageAccountName.blob.core.windows.net/$VhdsContainerName/$LinuxVM_Name-OS_Disk.vhd"|
        Add-AzureProvisioningConfig -Linux -LinuxUser "$LocalAdmin" -Password "$LocalPass" -Verbose |
        Add-AzureEndpoint -Name "HTTP" -PublicPort 80 -LocalPort 8080 -Protocol "tcp" |
        Set-AzureSubnet -SubnetNames "$VSubNet" |
        Set-AzureStaticVNetIP -IPAddress "$LinuxVM_IP" |
        Set-AzureVMExtension -ExtensionName "CustomScriptForLinux" -Publisher "Microsoft.OSTCExtensions" -Version "1.2" -PublicConfiguration $PublicConfiguration
Add-AzureDataDisk -CreateNew -HostCaching "None" -DiskLabel "DataDisk-1" -DiskSizeInGB 500 -LUN "1" -MediaLocation "https://$StorageAccountName.blob.core.windows.net/$VhdsContainerName/$LinuxVM_Name-Data_Disk1.vhd" -VM $vm
Add-AzureDataDisk -CreateNew -HostCaching "None" -DiskLabel "DataDisk-2" -DiskSizeInGB 500 -LUN "2" -MediaLocation "https://$StorageAccountName.blob.core.windows.net/$VhdsContainerName/$LinuxVM_Name-Data_Disk2.vhd" -VM $vm
New-AzureVM -ServiceName "$CloudSvcName" -Location "$AzureLocation" -VNetName "$VirtualNetwork" -VMs $vm -DeploymentName "$LinuxVM_Name-Provision" -Verbose
#-----------------------------------------------------
