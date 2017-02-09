# Setup Parts Unlimited with Jenkins Environment
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
    
