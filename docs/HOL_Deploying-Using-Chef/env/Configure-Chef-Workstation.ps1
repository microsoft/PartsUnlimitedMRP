[CmdletBinding()]
Param(
	[Parameter(Mandatory=$True)] [string] $chefUserName
)

# Set PowerShell execution policy
Set-ExecutionPolicy RemoteSigned -Force

# Install Chocolatey
iwr https://chocolatey.org/install.ps1 -UseBasicParsing | iex

refreshenv

# Install Chocolatey packages
& choco install poshgit -y
& choco install googlechrome -y
& choco install firefox -y
& choco install notepadplusplus -y
& choco install atom -y
& choco install putty -y
& choco install chefdk -y

refreshenv

# Set base git config
& git config --global user.email $chefUserName@partsunlimited.local
& git config --global user.name $chefUserName