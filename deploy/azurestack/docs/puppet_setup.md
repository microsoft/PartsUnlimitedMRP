# Continuous Deployment with Puppet
In this multi-part lab, we will set up the Puppet Master in Azure Stack, that will be used for Continuous Deployment of the Parts Unlimited MRP project. We'll then step through using that environment to showcase its CD capabilities. [Puppet](https://puppetlabs.com/) is a configuration management system that allows you to automate provisioning and configuration of machines by describing the state of your infrastructure as code. Infrastructure as Code is an important pillar of good DevOps.

## Prerequisites 
There are a couple of key things you'll need to have in place before setting up this lab environment, which, if you've been following the steps across other labs so far, you should already have most of them :-)

  - A configured Azure Stack, logged into MAS-CON01
  - The Azure Stack Tools downloaded to MAS-CON01 ([Details here](deploy/azurestack/docs/adding_vm_images.md))
  - An Ubuntu base image in the Platform Image Repository ([Details here](deploy/azurestack/docs/adding_vm_images.md))
  - Putty installed on MAS-CON01 (use the script below, from an administrative PowerShell console to download)
  
```powershell
Invoke-Webrequest https://the.earth.li/~sgtatham/putty/latest/x86/putty.exe -OutFile C:\putty.exe
```

Once you've got all those sorted, you're ready to deploy the environment. In this lab you will work with two machines: a Puppet Master machine and another machine known as a node which will host the MRP application. The only task you will perform on the node is to install the Puppet agent - the rest of the configuration will be applied by instructing Puppet how to configure the node though puppet programs on the Puppet Master.  Specifically, you'll perform:

  - Provisioning a Puppet Master and node (both Ubuntu VMs) in Azure Stack using ARM templates
  - Install Puppet Agent on the node
  - Configure the Puppet Environment
  - Test the Environment Configuration
  - Create a Puppet program to describe the environment for the MRP application
  
## Provisioning the Puppet Master | Deployment Options

Now, you have 2 options for deployment.

1. **ARM Template & Custom Deployment** -> for this option, you will perform a new custom template deployment from Azure Stack, using a JSON file that will be provided for you. You will enter a number of key values for the parameters, and then deploy. **This is the quicker option**.
2. **Create a Custom Marketplace Item for Deployment** -> for this option, in the same way you (optionally) added an Ubuntu 16.04-LTS item to the Azure Stack Marketplace, you will add a new .azpkg to your Azure Stack, and configure the deployment from this, providing the same parameters as per option 1.

**The end result of both of these options is the same, however if you'd like to populate your gallery with more items, use option 2.**

### *Option 1 - ARM Template & Custom Deployment
If you're not interested in creating a Marketplace item for Puppet, then this quick and easy approach should make things, well, quick and easy for you!

Firstly, from your MAS-CON01 machine, you need to click on the button below, and fill in the parameter fields. The link should open the Azure Stack portal, and if you're not already logged in, it'll prompt you for your Azure Stack credentials, then take you immediately to the custom template blade.

<a href="https://adminportal.local.azurestack.external/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fpuppet_standalone%2FPuppet.PuppetEnterprise%2FDeploymentTemplates%2FPuppetDeploy.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/docs/media/DeployToStack.png"/>
</a>

You'll need to enter information for the following fields:
- **PUPPETADMINPASSWORD** - choose a password of your choice.
- **PUPPETDNSNAMEFORPUBLICIP** - for testing purposes, use **pumrp-puppet**.
- **Resource Group** - for testing purposes, use **pumrp-puppet**.
- **Location** - seeing as this is Azure Stack, you'll just be able to choose local in the current technical preview.

![Jenkins Deployment](/deploy/azurestack/docs/media/JenkinsDeploy.PNG)

If you're interested in taking a deeper look at the ARM template that is used for deployment, you could either **click Edit Template** within the custom template deployment blade, and that will present the template that will be used for the deployment, or alternatively, you could **[grab the ARM template from here](/deploy/azurestack/instances/puppet_standalone/Puppet.PuppetEnterprise/DeploymentTemplates/PuppetDeploy.json)**

Depending on your hardware, the deployment of the key artifacts, the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!

Once the deployment has completed, you're ready to proceed with configuring the Puppet Master.
