[CmdletBinding()]
Param(
    [Parameter(Mandatory=$True)] [string] $sshTarget,
	[Parameter(Mandatory=$True)] [string] $sshUser,
    [Parameter(Mandatory=$True)] [string] $sshPassword
)

$ErrorActionPreference = "Stop"


# Get plink and psftp
$psftpExeUrl="https://the.earth.li/~sgtatham/putty/latest/x86/psftp.exe"
$plinkExeUrl="https://the.earth.li/~sgtatham/putty/latest/x86/plink.exe"

if (-not (Test-Path psftp.exe)) {
    wget $psftpExeUrl -OutFile psftp.exe
}
if (-not (Test-Path plink.exe)) {
    wget $plinkExeUrl -OutFile plink.exe
}

# Set deploy directory on target server
$deployDirectory = "/tmp/mrpdeploy_" + [System.Guid]::NewGuid().toString()

# Save sftp command text to file
$sftpFile = "sftp.txt"
$sftpContent = @'
mkdir ROOT_DEPLOY_DIRECTORY
cd ROOT_DEPLOY_DIRECTORY
mkdir deploy
cd deploy
put MongoRecords.js
put deploy_mrp_app.sh
chmod 755 deploy_mrp_app.sh
cd ..
put -r ../drop
cd drop
chmod 755 integration-service-0.1.0.jar
chmod 755 ordering-service-0.1.0.jar
chmod 755 mrp.war
'@
$sftpContent = $sftpContent.Replace('ROOT_DEPLOY_DIRECTORY',$deployDirectory)
Set-Content -Path $sftpFile -Value $sftpContent


# Save plink command text to file
$plinkFile = "plink.txt"
$plinkContent = @'
cd ROOT_DEPLOY_DIRECTORY/deploy
sudo apt-get install dos2unix -y
dos2unix deploy_mrp_app.sh
sudo bash ./deploy_mrp_app.sh
'@
$plinkContent = $plinkContent.Replace('ROOT_DEPLOY_DIRECTORY',$deployDirectory)
Set-Content -Path $plinkFile -Value $plinkContent

# Copy files and execute MRP deployment shell script
echo n | & .\psftp.exe $sshUser@$sshTarget -pw $sshPassword -b $sftpFile 
echo n | & .\plink.exe $sshUser@$sshTarget -pw $sshPassword -m $plinkFile