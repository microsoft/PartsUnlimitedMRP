#Getting Started with DevOps on Azure Stack#

In order to get started exploring DevOps tooling on Azure Stack, you're going to need an Azure Stack, and that requires some hardware. If you're not sure what specific hardware you'll need for the single-node POC configuration, don't fear, it's all [documented here]
(https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-deploy).

Once you've sourced your hardware, you'll need to [pull down the package](https://azure.microsoft.com/en-us/overview/azure-stack/try/?v=try), which requires you to complete a short registration form, then download the bits. A word of warning though - it's a 20GB file, so set aside a bit of time for a coffee, or 9.

With the package safely downloaded, [follow the documentation](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-run-powershell-script) to prepare your deployment machine, extract the necessary bits, and then run the PowerShell deployment script which will essentially deploy Azure Stack on your hardware. A couple of hours later, you'll have yourself an Azure Stack! There are a few examples of deployments scripts within the official documentation that should help you configure your Azure Stack deployment optimally, specifically around the use of Static IP addresses, the use of a Time Server etc. Read the docs carefully.

Make sure you pay particularly close attention to the password expiration configuration, as you don't want your POC machine to expire too soon!

# Next Step

Once your deployment has completed, we can move on to [connecting to Azure Stack](/deploy/azurestack/docs/connect_to_azurestack.md), and from there, we'll upload some images that you can use with the DevOps tools we'll be deploying.

##*Important Note for Azure Stack TP2 & Static IP Addresses#

When you deployed your Azure Stack, if you deployed with a static network configuration, there is a known issue, and workaround. If you're not sure whether you deployed with a static network configuration, essentially, you have a static configuration if you used any of the following parameters within your deployment script:

- NatIPv4Subnet
- NatIPv4Address
- NatIPv4DefaultGateway

###Known Issue#

If you deployed an Azure Stack TP2 One-Node environment with a static network configuration, you have 10 days to activate the MAS-BGPNAT01 VM, before it is no longer licensed. If the MAS-BGPNAT01 VM is off for an extended period, your environment may no longer function, even if it is activated/licensed and running.

###Resolution#

To update the static network configuration and activate the MAS-BGPNAT01 VM, take the following steps:

- From Hyper-V, Connect to and Login to the MAS-BGPNAT01 VM as azurestack\administrator (switch users from azurestack\fabricadmin)
- Open PowerShell ISE (this will open as Administrator, which is required)
- Create a new file, paste (from Clipboard via Hyper-V console) in and Run the following script (to eliminate any strange characters coming through on the copy/paste from this page, it is recommended that you copy from here, paste into notepad, then into PowerShell ISE):

```PowerShell
$NetNatName = "BGPNAT"
$NetNatEA = Get-NetNatExternalAddress -NatName $NetNatName
$IPAddress = $NetNatEA.IPAddress
$ExternalAddressID = $NetNatEA.ExternalAddressID
$IPAddressConfig = Get-NetIPAddress -IPAddress $IPAddress
$InterfaceAlias = $IPAddressConfig.InterfaceAlias
$PrefixLength = $IPAddressConfig.PrefixLength
Remove-NetNatExternalAddress -ExternalAddressID $ExternalAddressID -Confirm:$false
New-NetIPAddress -InterfaceAlias $InterfaceAlias -IPAddress $IPAddress -PrefixLength $PrefixLength
Add-NetNatExternalAddress -NatName $NetNatName -IPAddress $IPAddress -PortStart 4096 -PortEnd 49151
```

Wait for the network/internet connection to return. Sometime soon after the script is run, the Network task bar icon will go from warning to clear. At this point, you can check the System settings for activation – it will attempt to activate on its own, or you can choose to activate it manually. The desired end state after these actions is as follows:

- Logoff the MAS-BGPNAT01 VM
- Verify that you can continue to use the Portal / API for your Azure Stack TP2 One-Node environment
- If you can no longer access the Portal / API for your environment (and you are past the 10-day grace period), you will have to redeploy, and then perform the above steps within the initial 10-day grace period
