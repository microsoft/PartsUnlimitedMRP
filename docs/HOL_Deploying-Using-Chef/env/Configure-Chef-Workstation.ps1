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
& choco install putty -y
& choco install chefdk -y

refreshenv