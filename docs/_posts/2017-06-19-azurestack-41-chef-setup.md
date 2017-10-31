---
layout: page
title:  Setup Continuous Deployment with Chef
category: AzureStack
order: 11
---
In this multi-part lab, we will setup a Chef Server in Azure Stack, that will be used for Continuous Deployment of the Parts Unlimited MRP project. This hands-on lab is designed to point out new features, discuss and describe them, and enable you to understand these features as part of the DevOps Lifecycle.

### Prerequisites
There are a couple of key things you'll need to have in place before setting up this lab environment, which, if you've been following the steps across other labs so far, you should already have most of them :-)

 - A configured Azure Stack, logged into the Azure Stack Development kit host
  - The Azure Stack Tools downloaded to the Azure Stack Development kit host ([Details here](azurestack-33-images.html#connecting-to-azure-stack-via-powershell))
  - An Ubuntu base image in the Platform Image Repository ([Details here](azurestack-33-images.html#add-vm-image-to-platform-image-repository-with-powershell))
  - Putty installed on the Azure Stack Development kit host (use the script below, from an administrative PowerShell console to download)
  
```powershell
Invoke-Webrequest https://the.earth.li/~sgtatham/putty/latest/x86/putty.exe -OutFile C:\putty.exe
```

Once you've got all those sorted, you're ready to deploy the environment. In this lab you will work with 3 machines: a Chef Server, a Windows-based workstation machine, and Linux-based machine which will host the MRP application  In terms of tasks, specifically, you'll perform:

- Provision the Lab: This step walks you through how to set up the environment through syndication of images and an ARM template.
- Configure the Chef Workstation: You will learn how to set up the Chef Starter Kit on the workstation.
- Create a Cookbook: You will create an MRP cookbook and create a recipe for the MRP app’s dependencies.
- Create a Role: This step will show you how to create a role to define a baseline set of cookbooks and attributes that can be applied to multiple servers.
- Bootstrap the MRP App Server and Deploy the Application: You will bootstrap the MRP app and use the role that you previously created to deploy the app.
- Remediating Configuration Changes: You will see how Chef reacts when changes happen to the configuration and how Chef resolves issues.

### Provision the Lab | Enable Syndication
In previous labs, you'll have seen that whether we are deploying Jenkins, Puppet, or just a regular Linux VM, we've been using an Ubuntu image that we added to the Azure Stack Platform Image Repository ([earlier](azurestack-33-images.html#add-vm-image-to-platform-image-repository-with-powershell)).  We essentially 'sideloaded' this image into Azure Stack, and from there, we used it with a number of ARM templates to deploy specific workloads.

There is, however, another way to populate your Azure Stack Platform Image Repository, and also, the Azure Stack Marketplace.  This alternative method is known as **Marketplace Syndication**.

**Marketplace syndication** essentially allows you to utilize content available from the Azure marketplace, in Azure Stack. You can download from a curated list of Azure marketplace items that have been pre-tested to run on Azure Stack. New items are frequently added to this list, so it's important to make sure you check back for new content.

To download marketplace items into Azure Stack, you've first got to register Azure Stack with Azure.

#### Register Azure Stack with Azure

To register your Azure Stack with Azure, you'll need to perform a few steps **from the Azure Stack Development kit host**.  Before registering, you'll need the following info:

- The subscription ID for an Azure subscription. To get this, sign in to Azure, click More services > Subscriptions, click the subscription you want to use, and under Essentials you can find the Subscription ID. China, Germany, US Government cloud are not currently supported.
- The username and password for an account that is an owner for the subscription (MSA/2FA accounts are supported).
- The AAD directory for the Azure subscription. You can find this directory in Azure by hovering over your avatar at the top right corner of the Azure portal.

If you don’t have an Azure subscription that meets these requirements, you can [create a free Azure account here](https://azure.microsoft.com/en-us/free/?b=17.06). Registering Azure Stack incurs no cost on your Azure subscription.

Once you have those details, you're ready to register your Azure Stack, by [following the steps outlined here](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-register). Remember, run these steps **from the Azure Stack Development Kit Host**.

#### Verify the registration

1. Sign in to the Azure Stack portal as a service administrator.
2. Click **More Services** > **Marketplace Management** > **Add from Azure**.
3. If you see a list of items available from Azure (such as WordPress), your activation was successful.

### Provision the Lab | Syndicate Windows Server

For the purpose of this lab, we're going to select to syndicate the Windows Server 2012 R2 Datacenter - Eval gallery item.  This is smaller than the 2016 version, and thus quicker to download and test.  In order to kick off the syndication, perform the following steps:

1. Ensure you are logged into **the Azure Stack Development kit host** and signed in to the Azure Stack portal as a service administrator.
2. Click **More Services** > **Marketplace Management** > **Add from Azure**.  You should see a list of available options, including multiple Windows Server offerings:

  ![Successful Upload](<../assets/azurestack/WindowsServerSyndication.PNG>)
  
3. As mentioned earlier, click on **Windows Server 2012 R2 Datacenter - Eval** and when the new blade opens, click on **Download**

The download will take a while, depending on your connection speed.  You will recieve a notification in the Azure Stack portal once it's complete.  The benefit of this approach is that the VHD will be downloaded into the Azure Stack Platform Image Repository, and in addition, a gallery item and default ARM template will also appear in your Azure Stack Marketplace view, so tenants/users will be able to deploy new instances of Windows Server 2012 R2 on your Azure Stack, and you haven't had to build any custom packages to enable the functionality.

### Provision the Lab | Chef Server Deployment Options

With the Windows Server image downloading, we can turn our attention to preparing for deployment of a Chef Server within Azure Stack.  As it stands, there is no Chef offering available for syndication today, thus, we must sideload our own, as we have done for the previous labs, for both Jenkins and Puppet.

Now, you have 2 options for deployment.

1. **ARM Template & Custom Deployment** -> for this option, you will perform a new custom template deployment from Azure Stack, using a JSON file that will be provided for you. You will enter a number of key values for the parameters, and then deploy. **This is the quicker option**.
2. **Create a Custom Marketplace Item for Deployment** -> for this option, in the same way you (optionally) added an Ubuntu 16.04-LTS item to the Azure Stack Marketplace, you will add a new .azpkg to your Azure Stack, and configure the deployment from this, providing the same parameters as per option 1.

**The end result of both of these options is the same, however if you'd like to populate your gallery with more items, use option 2.**

#### *Option 1 - ARM Template & Custom Deployment
If you're not interested in creating a Marketplace item for Chef Server, then this quick and easy approach should make things, well, quick and easy for you!

Firstly, from your Azure Stack Development kit host machine, you need to click on the button below, and fill in the parameter fields. The link should open the Azure Stack portal, and if you're not already logged in, it'll prompt you for your Azure Stack credentials, then take you immediately to the custom template blade.

<a href="https://adminportal.local.azurestack.external/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fchef_standalone%2FChef.ChefServer%2FDeploymentTemplates%2FChefDeploy.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/assets/azurestack/DeployToStack.png"/>
</a>

You'll need to enter information for the following fields:

- **CHEFADMINUSERNAME** - choose a username of your choice. For testing purposes, use **chefadmin**
- **CHEFADMINPASSWORD** - choose a password of your choice.
- **CHEFDNSNAMEFORPUBLICIP** - for testing purposes, use **chefserver**.
- **Resource Group** - for testing purposes, use **chefmrp**.
- **Location** - seeing as this is the Azure Stack Develelopment Kit, you'll just be able to choose local for now.

![Chef Deployment](<../assets/azurestack/ChefDeploy.PNG>)

If you're interested in taking a deeper look at the ARM template that is used for deployment, you could either **click Edit Template** within the custom template deployment blade, and that will present the template that will be used for the deployment, or alternatively, you could **[grab the ARM template from here](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/instances/chef_standalone/Chef.ChefServer/DeploymentTemplates/ChefDeploy.json)**

Depending on your hardware, the deployment of the key artifacts, the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!

Once the deployment has completed, you're ready to proceed with deploying the additional resources.

#### *Option 2 - Create a Custom Marketplace Item for Deployment
If you are interested in adding a custom marketplace item for Chef Server, to your Azure Stack Marketplace, then these steps will help. I've already made the package for you, so you should just be able to follow these steps, and import it right into your Azure Stack.

As we saw earlier, when we [added our Ubuntu base image to the Azure Stack marketplace](azurestack-34-marketplace.html), things are much easier when something is packaged for you, so to start things off, pull down the .azpkg file for our Chef environment, that I've stored on GitHub. From your **Azure Stack Development kit host** machine, do the following:

- [Download Chef Server Package](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/deploy/azurestack/instances/chef_standalone/Chef.ChefServer.1.0.0.azpkg?raw=true)

1. Navigate to your **Chef.ChefServer.1.0.0.azpkg** file, you downloaded earlier
2. Move it to a newly created folder **C:\MyMarketPlaceItems**.

  It’s important to note that if you are going to use the package I have provided, you need to have used the following info when you uploaded your Ubuntu base VHD image to the platform image repository [earlier](azurestack-33-images.html). Any differences, and the package I’m providing will not reference your uploaded image. If you used an exact copy of my PowerShell upload script, you're all set.
    
    - Publisher "Canonical"
    - Offer "UbuntuServer"
    - SKU "16.04.3-LTS"
    
Now that we have the package ready to upload, we need *somewhere* in Azure Stack to upload it to. Fortunately, we [created a storage account for this very purpose earlier](azurestack-34-marketplace.html#uploading-a-package-to-azure-stack), so we'll use the same storage account for this package.

1. Connect to your Azure Stack via an **administrative PowerShell console**. If you're not still connected from the earlier steps, run the following:
  
  ``` powershell
  cd\
  cd C:\AzureStack-Tools-master\connect
  Import-Module .\AzureStack.Connect.psm1
  Add-AzureStackAzureRmEnvironment -Name "AzureStackAdmin" -ArmEndpoint "https://adminmanagement.local.azurestack.external"
  $TenantID = Get-DirectoryTenantID -AADTenantName "<myaadtenant>.onmicrosoft.com or customdomain.com" -EnvironmentName AzureStackAdmin
  $UserName='<username@myaadtenant.onmicrosoft.com or username@customdomain.com>'
  $Password='<administrator or user password>'| `
  ConvertTo-SecureString -Force -AsPlainText
  $Credential= New-Object PSCredential($UserName,$Password)
  Login-AzureRmAccount -EnvironmentName "AzureStackAdmin" -TenantId $TenantID -Credential $Credential
  ```
  
2. Now, let's access the storage account to hold this package. If you recall, we named the storage account **tenantartifacts** and this is located in a dedicated **resource group** of the same name:

  ``` powershell
  $StorageAccount = Get-AzureRmStorageAccount -ResourceGroupName tenantartifacts -Name tenantartifacts
  $GalleryContainer = Get-AzureStorageContainer -Name gallery -Context $StorageAccount.Context
  ```
3. With the resource group, storage account and gallery container now accessible, we can push our new Chef Server marketplace package into Azure Stack.

  ``` powershell
  $GalleryContainer | Set-AzureStorageBlobContent -File "C:\MyMarketPlaceItems\Chef.ChefServer.1.0.0.azpkg"
  $GalleryItemURI = (Get-AzureStorageBlob -Context $StorageAccount.Context -Blob 'Chef.ChefServer.1.0.0.azpkg' -Container 'gallery').ICloudBlob.uri.AbsoluteUri
  Add-AzureRMGalleryItem -GalleryItemUri $GalleryItemURI -Verbose
  ```

When successful, you should see a **StatusCode** of **Created**

   ![Successful Upload](<../assets/azurestack/PSCreated.PNG>)

Go back and refresh the portal, and under **New -> Virtual Machines -> See All**, you should see your newly added Chef Server marketplace item

  ![Chef Server added to Marketplace](<../assets/azurestack/ChefMarketplace.PNG>)
  
With your newly created marketplace item created and pushed to the Azure Stack Marketplace, we're ready to deploy an instance of the environment.

1. On the **Azure Stack Development kit host** machine, in your Azure Stack portal, click on **New**, then **Virtual Machines**, then **See all**.
2. Select the **Chef Server** item in the marketplace, and click **Create**.
3. Provide the information for the following fields:
- **CHEFADMINUSERNAME** - choose a username of your choice. For testing purposes, use **chefadmin**
- **CHEFADMINPASSWORD** - choose a password of your choice.
- **CHEFDNSNAMEFORPUBLICIP** - for testing purposes, use **chefserver**.
- **Resource Group** - for testing purposes, use **chefmrp**.
- **Location** - seeing as this is the Azure Stack Development Kit, you'll just be able to choose local for now.
  
  Once you've filled in the fields, it should look like this:
  
  ![Deploying Chef](<../assets/azurestack/ChefDeploy.PNG>)
 
4. Click **OK** to confirm the parameters, and then **Create** to start the deployment.

Depending on your hardware, the deployment of the key artifacts, the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!
Once the deployment has completed, you're ready to proceed with configuring the rest of the environment.

### Provision the Lab | Deploy Windows Server Workstation
With out Chef Server deployed, we can move on to deploying our Chef Workstation, which we'll use for administration of the Chef environment.  In this lab, it will be a Windows Server-based virtual machine, that we will deploy into the same resource group and virtual network as the existing Chef Server. That way, the 2 VMs can talk to one another easily, resolve DNS easily etc.  It also simplifies management with them within this small POC network.

Now in order to streamline this, I've created an ARM template for you to use - all you need to do is grab it from here:

- **[Deploy Chef Workstation](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/instances/chef_workstation/AddChefWorkstation.json)**

Alternatively, for those of you who like to press buttons, and would like a simple option for deploying without copying and pasting, click the button below from your **Azure Stack Development kit host** machine, and fill in the parameter fields. The link should open the Azure Stack admin portal, and if you're not already logged in, it'll prompt you for your Azure Stack credentials, then take you immediately to the custom template blade:

<a href="https://adminportal.local.azurestack.external/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fchef_workstation%2FAddChefWorkstation.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/assets/azurestack/DeployToStack.png"/>
</a>

You'll need to enter information for the following fields:
- **ADMINUSERNAME** - accept the default of **localadmin**.
- **ADMINPASSWORD** - choose a password of your choice.
- **DNSNAMEFORPUBLICIP** - for testing purposes, use **chefworkstation**.
- **Resource Group** - for testing purposes, select existing, and use the drop down to select **chefmrp**.
- **Location** - this will already be selected based on the resource group you choose.

![Chef Workstation Deployment](<../assets/azurestack/ChefWorkstationDeploy.PNG>)

If you're interested in taking a deeper look at the ARM template that is used for deployment, you could either **click Edit Template** within the custom template deployment blade, and that will present the template that will be used for the deployment.

Depending on your hardware, the deployment of the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!

You dont have to wait for it to complete, instead, we can move on to deploying our 3rd virtual machine, that will be managed by Chef -this will be our Linux node, that will be based on the Ubuntu image that's already within our Platform Image Respository.

### Provision the Lab | Deploy Linux MRP Node
With out Chef Server and Workstation deployed, we can move on to deploying a node which we'll manage with Chef, and deploy our Parts Unlimited MRP app onto.  This will be a Linux node, deployed into the same resource group and virtual network as the other two virtual machines, thus making communication between the VMs, easy.

Now in order to streamline this, I've created an ARM template for you to use - all you need to do is grab it from here:

- **[Deploy Chef Node](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/instances/chef_node/AddChefNode.json)**

Alternatively, for those of you who like to press buttons, and would like a simple option for deploying without copying and pasting, click the button below from your **Azure Stack Development kit host** machine, and fill in the parameter fields. The link should open the Azure Stack admin portal, and if you're not already logged in, it'll prompt you for your Azure Stack credentials, then take you immediately to the custom template blade:

<a href="https://adminportal.local.azurestack.external/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fchef_node%2FAddChefNode.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/assets/azurestack/DeployToStack.png"/>
</a>

You'll need to enter information for the following fields:
- **ADMINUSERNAME** - accept the default of **localadmin**.
- **ADMINPASSWORD** - choose a password of your choice.
- **DNSNAMEFORPUBLICIP** - for testing purposes, use **chefnode1**.
- **Resource Group** - for testing purposes, select existing, and use the drop down to select **chefmrp**.
- **Location** - this will already be selected based on the resource group you choose.

![Chef Node Deployment](<../assets/azurestack/AddChefNode.PNG>)

If you're interested in taking a deeper look at the ARM template that is used for deployment, you could either **click Edit Template** within the custom template deployment blade, and that will present the template that will be used for the deployment.

Depending on your hardware, the deployment of the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!

Once this is complete, we're ready to move on to configuring the environment!

## Next steps

In this lab, you learned how to deploy Chef Server on Azure Stack, how to enable and use Marketplace Syndication with Azure Stack, deploying a syndicated Windows Server machine that will be used as a Workstation, and finally, deploy an additional Linux node, that will be managed by Chef.  In the next lab, you'll walk through deploying the Parts Unlimited MRP app, to this node, from Chef. 

- [Parts Unlimited MRP Continous Deployment with Chef](azurestack-42-chef-cd.html)

### Continuous Feedback

##### Issues / Questions about this Hands-On-Lab?

[If you are encountering issues or have questions during this Hands on Labs, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)
