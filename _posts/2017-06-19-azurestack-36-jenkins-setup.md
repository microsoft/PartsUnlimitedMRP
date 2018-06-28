---
layout: page
title:  Setup Parts Unlimited MRP with Jenkins
category: AzureStack
order: 6
---
In this multi-part lab, we will set up the Jenkins Master in Azure Stack, that will be used for the Parts Unlimited MRP project. We'll then step through using that environment to showcase CI/CD capabilities. [Jenkins](https://jenkins.io/) is an open source automation server that provides capabilities for building, deploying and automating any project.

### Prerequisites 
There are a couple of key things you'll need to have in place before setting up this lab environment, which, if you've been following the steps so far, you should already have most of them :-)

  - A configured Azure Stack, logged into the Azure Stack Development Kit host
  - The Azure Stack Tools downloaded to the Azure Stack Development Kit host ([Details here](azurestack-33-images.html#connecting-to-azure-stack-via-powershell))
  - An Ubuntu base image in the Platform Image Repository ([Details here](azurestack-33-images.html#add-vm-image-to-platform-image-repository-with-powershell))
  - Putty installed on the Azure Stack Development Kit host (use the script below, from an administrative PowerShell console to download)
  - An Oracle Account, in order to source the latest JDK (Test creds at http://www.oracle.com then click Sign-in at the top)

```powershell
Invoke-Webrequest https://the.earth.li/~sgtatham/putty/latest/x86/putty.exe -OutFile C:\putty.exe
```
Once you've got all those sorted, you're ready to deploy the environment. The environment will consist of a single **resource group** containing the Jenkins Master VM, connected to a single virtual network. Later in the lab, you'll deploy another standard Ubuntu Linux VM, that will be configured using Jenkins.

### Deployment Options

Now, you have 2 options for deployment.

1. **ARM Template & Custom Deployment** -> for this option, you will perform a new custom template deployment from Azure Stack, using a JSON file that will be provided for you. You will enter a number of key values for the parameters, and then deploy. **This is the quicker option**.
2. **Create a Custom Marketplace Item for Deployment** -> for this option, in the same way you (optionally) added an Ubuntu 16.04-LTS item to the Azure Stack Marketplace, you will add a new .azpkg to your Azure Stack, and configure the deployment from this, providing the same parameters as per option 1.

**The end result of both of these options is the same, however if you'd like to populate your gallery with more items, use option 2.**

#### *Option 1 - ARM Template & Custom Deployment
If you're not interested in creating a Marketplace item for 'Parts Unlimited MRP with Jenkins', then this quick and easy approach should make things, well, quick and easy for you!

Firstly, from your the Azure Stack Development Kit host machine, you need to click on the button below, and fill in the parameter fields. The link should open the Azure Stack portal, and if you're not already logged in, it'll prompt you for your Azure Stack credentials, then take you immediately to the custom template blade.

<a href="https://adminportal.local.azurestack.external/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdeploy%2Fazurestack%2Finstances%2Fjenkins_standalone%2FTheJenkinsProject.Jenkins%2FDeploymentTemplates%2FJenkinsDeploy.json" target="_blank">
        <img src="https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/assets/azurestack/DeployToStack.png"/>
</a>

You'll need to enter information for the following fields:
- **JENKINSADMINPASSWORD** - choose a password of your choice.
- **JENKINSDNSNAMEFORPUBLICIP** - for testing purposes, use **pumrp-jenkins**.
- **Resource Group** - for testing purposes, use **pumrp-jenkins**.
- **Location** - seeing as this is Azure Stack, you'll just be able to choose local in the current technical preview.

![Jenkins Deployment](<../assets/azurestack/JenkinsDeploy.PNG>)

If you're interested in taking a deeper look at the ARM template that is used for deployment, you could either **click Edit Template** within the custom template deployment blade, and that will present the template that will be used for the deployment, or alternatively, you could **[grab the ARM template from here](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/azurestack/instances/jenkins_standalone/TheJenkinsProject.Jenkins/DeploymentTemplates/JenkinsDeploy.json)**

Depending on your hardware, the deployment of the key artifacts, the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!

Once the deployment has completed, you're ready to proceed with configuring Jenkins.

#### *Option 2 - Create a Custom Marketplace Item for Deployment
If you are interested in adding a custom marketplace item to your Azure Stack Marketplace, then these steps will help. I've already made the package for you, so you should just be able to follow these steps, and import it right into your Azure Stack.

As we saw earlier, when we [added our Ubuntu base image to the Azure Stack marketplace](azurestack-34-marketplace.html), things are much easier when something is packaged for you, so to start things off, pull down the .azpkg file for our Jenkins environment, that I've stored on GitHub. From yor **the Azure Stack Development Kit host** machine, do the following:

- [Download Jenkins Package](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/deploy/azurestack/instances/jenkins_standalone/TheJenkinsProject.Jenkins.1.0.0.azpkg?raw=true)

1. Navigate to your **TheJenkinsProject.Jenkins.1.0.0.azpkg** file, you downloaded earlier
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
3. With the resource group, storage account and gallery container now accessible, we can push our new Jenkins marketplace package into Azure Stack.

  ``` powershell
  $GalleryContainer | Set-AzureStorageBlobContent -File "C:\MyMarketPlaceItems\TheJenkinsProject.Jenkins.1.0.0.azpkg"
  $GalleryItemURI = (Get-AzureStorageBlob -Context $StorageAccount.Context -Blob 'TheJenkinsProject.Jenkins.1.0.0.azpkg' -Container 'gallery').ICloudBlob.uri.AbsoluteUri
  Add-AzureRMGalleryItem -GalleryItemUri $GalleryItemURI -Verbose
  ```

When successful, you should see a **StatusCode** of **Created**

   ![Successful Upload](<../assets/azurestack/PSCreated.PNG>)

Go back and refresh the portal, and under **New -> Virtual Machines -> See All**, you should see your newly added Jenkins marketplace item

  ![Jenkins added to Marketplace](<../assets/azurestack/JenkinsMarketplace.PNG>)
  
With your newly created marketplace item created and pushed to the Azure Stack Marketplace, we're ready to deploy an instance of the environment.

1. On the **the Azure Stack Development Kit host** machine, in your Azure Stack portal, click on **New**, then **Virtual Machines**, then **See all**.
2. Select the **Jenkins** item in the marketplace, and click **Create**.
3. Provide the information for the following fields:
  - **JENKINSADMINPASSWORD** - choose a password of your choice.
  - **JENKINSDNSNAMEFORPUBLICIP** - for testing purposes, use **pumrp-jenkins**. 
  - **Resource Group** - for testing purposes, use **pumrp-jenkins**.
  - **Location** - seeing as this is Azure Stack, you'll just be able to choose local in the current technical preview.
  
  Once you've filled in the fields, it should look like this:
  
  ![Deploying Jenkins](<../assets/azurestack/JenkinsDeploy.PNG>)
 
4. Click **OK** to confirm the parameters, and then **Create** to start the deployment.

Depending on your hardware, the deployment of the key artifacts, the virtual machine, and its respective automated configuration, may take a while. Expect around 20-30 mins for the deployment, unless you have new hardware, and a bank of SSDs for storage!
Once the deployment has completed, you're ready to proceed with configuring Jenkins.

### Jenkins Setup
Regardless of using Option 1, or Option 2, your environment should now be deployed. You can now continue with the configuration of the Jenkins environment specifically.

1. Firstly, we need to **obtain the public IP of the Jenkins Master VM**. In the Azure Stack portal, click on **Resource Groups** and look for the Resource Group that you have just created.  Click on the virtual machine in the resource group (_pumrp-jenkins_ in this example) and look for the "Public IP address/DNS name label".

    ![Obtain the public IP of the Jenkins Master](<../assets/azurestack/JenkinsMRPDeployed.PNG>)

2. We can now **SSH to the Jenkins Master VM**. To do this, we'll use PuTTY, (or any other ssh tool that you like) to connect to the IP address of the VM, and once connected type the following information to login to the VM:

    - login as: jenkinsadmin
    - password: your password chosen for **JENKINSADMINPASSWORD** at template deployment time
    
To make this lab easier, the user has been pre-configured to be **jenkinsadmin** through the automated deployment.

#### Configure your Jenkins Master
In this task, we will perform the basic configuration of the Jenkins master server and install the necessary plugins that will be used for Continuous Integration.

**1.** Obtain the initial admin password

From the SSH session that you have opened at the end of Task 1, type the following command to obtain the initial admin password.

``` bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Copy the value returned by the command.  Keep the SSH session open, we will return at the end of this task.

![Initial jenkins admin password](<../assets/azurestack/JenkinsPassword.PNG>) 

**2.** Unlock the jenkins master

On the Azure Stack Development Kit host, with your browser, navigate to the default page of the Jenkins master. 

```
http://ip_address_of_your_jenkinsmaster:8080
```

Paste the initial admin password obtained earlier to unlock your instance of Jenkins and click **Continue**.

![Initial jenkins admin password](<../assets/azurestack/initial_jenkins_unlock.png>)

Click **Install suggested plugins** 

![Initial jenkins plugins](<../assets/azurestack/initial_jenkins_plugins.png>)

**3.** Create the first user 

Create a user from the "Create First Admin User": 
* Username = Jenkins
* Password = Passw0rd
* Full name = Jenkins Admin
* E-mail address = jenkins@microsoft.com
* Click **Save and Finish**. 

![Initial jenkins plugins](<../assets/azurestack/JenkinsCreateAdminUser.PNG>)

**4.** Start Using Jenkins

Click the **Start Using Jenkins** button to get started! You now have a virtual machine in Azure Stack running Jenkins as a Master. You should automatically be logged in as the user you have just created, but if not, logon to the Jenkins Master with the credentials that you have just created in step 4. 

* Username = Jenkins 
* Password = Passw0rd

**5.** Navigate to the "Configure System" page:

From the left-hand navigation, click on **Manage Jenkins**, then **Configure System**

Under **Jenkins Location** look for the Jenkins URL field and type the URL of your Jenkins master: http://ip_address_of_your_jenkinsmaster:8080/

**NOTE:** the URL may already be here but type it again and save to ensure the proper completion of the rest of the lab.

![Jenkins URL](<../assets/azurestack/JenkinsLocation.PNG>)

Click **Save** 

**6.** Install git

Switch to the SSH session that was opened during step 1.

Type the following command:
```
sudo apt-get install git -y
```

#### Configure Jenkins  
The three tools that we will need to create our pipeline on Jenkins needs to be configured on our instance of Jenkins.

Connect to the Jenkins master that you have configured in the previous task using port 8080:

```
http://ip_address_of_your_jenkinsmaster:8080/manage 
```

**1.** Configure the JDK

From the Jenkins portal, on the left-hand navigation, click on **Manage Jenkins**, then **Global Tool Configuration**.  In order to build the Parts Unlimited application we need to have the JDK installed.

Click on **Add JDK**

* Type the friendly name for the JDK: JDK 8
* Ensure the box "Install automatically" is checked
* In the drop-down list, select the latest version ("Java SE Development Kit 8u131" at the time of writing)
* Check the box "I agree to the Java SE Development Kit License Agreement"
* Click on the link to enter the username and password of your Oracle account (Test credentials here: https://www.oracle.com and click on Sign In at the top) then click **OK** then **Close**
* Click **Save**

![JDK Installation](<../assets/azurestack/JDKSettings.PNG>)

**2.** Configure Gradle 

Once you've been returned to the Manage Jenkins page, go to the **Global Tool Configuration** again.

Gradle will be used to build the Parts Unlimited MRP application. If needed, you could use Maven or Ant as well, the configuration would be very similar. Click on **Add Gradle**

* Type the friendly name for this installation of Gradle: "Gradle"
* Verify that the "Install automatically" box is checked
* Select the latest version of Gradle in the drop-down list.
* Click **Save**

![Gradle Installation](<../assets/azurestack/JenkinsGradle.PNG>)

## Next steps

In this lab, you learned how to setup a Jenkins Master on Azure Stack, try these labs for the next steps: 

- [Parts Unlimited MRP Continous Integration with Jenkins](azurestack-37-jenkins-ci.html)
- [Parts Unlimited MRP Continous Deployment with Jenkins](azurestack-38-jenkins-cd.html)

## Continuous Feedback

#### Issues / Questions about this Hands-On-Lab?

[If you are encountering issues or have questions during this Hands on Labs, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)
