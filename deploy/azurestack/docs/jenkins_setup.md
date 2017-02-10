# Setup Environment | Parts Unlimited MRP with Jenkins
In this multi-part lab, we will set up the Jenkins Master in Azure Stack, that will be used for the Parts Unlimited MRP project. We'll then step through using that environment to showcase CI/CD capabilities. [Jenkins](https://jenkins.io/) is an open source automation server that provides capabilities for building, deploying and automating any project.

## Prerequisites 
There are a couple of key things you'll need to have in place before setting up this lab environment, which, if you've been following the steps so far, you should already have them :-)

  - A configured Azure Stack, logged into MAS-CON01
  - The Azure Stack Tools downloaded to MAS-CON01 ([Details here](deploy/azurestack/docs/adding_vm_images.md))
  - An Ubuntu base image in the Platform Image Repository ([Details here](deploy/azurestack/docs/adding_vm_images.md))
  - Putty installed on MAS-CON01 (use the script below, from an administrative PowerShell console to download)

```powershell
Invoke-Webrequest https://the.earth.li/~sgtatham/putty/latest/x86/putty.exe -OutFile C:\putty.exe
```
Once you've got all those sorted, you're ready to deploy the environment. The environment will consist of a single **resource group** containing 2 virtual machines, connected on a single virtual network. One of the virtual machines will be the **Jenkins Master**, while the other will be a standard Ubuntu Linux VM, that will be configured using Jenkins, later in the lab series.

## Deployment Options

Now, you have 2 options for deployment.

1. **ARM Template & Custom Deployment** -> for this option, you will perform a new custom template deployment from Azure Stack, using a JSON file that will be provided for you. You will enter a number of key values for the parameters, and then deploy. **This is the quicker option**.
2. **Create a Custom Marketplace Item for Deployment** -> for this option, in the same way you added an Ubuntu 14.04-LTS item to the Azure Stack Marketplace, you will add a new .azpkg to your Azure Stack, and configure the deployment from this, providing the same parameters as per option 1.

**The end result of both of these options is the same, however if you'd like to populate your gallery with more items, use option 2.

### *Option 1 - ARM Template & Custom Deployment
<<<<<<< HEAD
If you're not interested in creating a Marketplace item for 'Parts Unlimited MRP with Jenkins', then this quick and easy approach should make things, well, quick and easy for you!

Firstly, you need to click on the button below, and then enter some information:

<a href="https://portal.azurestack.local/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fjenkins_mrp%2FPartsUnlimitedMRP.MRPwithJenkins%2FDeploymentTemplates%2FMRPwithJenkinsDeploy.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/docs/media/DeployToStack.png"/>
</a>
=======
>>>>>>> 1dfa10603ffd4ccdc00834c74195be41f7f50f7c
