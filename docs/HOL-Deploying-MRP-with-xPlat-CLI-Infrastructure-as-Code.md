# Deploying MRP with xPlat CLI Infrastructure as Code #

The Azure Cross-Platform Command-Line Interface (xplat-cli) provides a set of open source, cross-platform commands for working with the Azure Platform. The xplat-cli provides much of the same functionality found in the Azure Management Portal, such as the ability to manage websites, virtual machines, mobile services, SQL Database and other services provided by the Azure platform.

The xplat-cli is written in JavaScript, and requires Node.js. It is implemented using the Azure SDK for Node.js, and released under an Apache 2.0 license. The project repository is located at [https://github.com/WindowsAzure/azure-sdk-tools-xplat](https://github.com/WindowsAzure/azure-sdk-tools-xplat).

In this hands-on lab, you will learn how to install and configure the Azure Cross-Platform Command-Line Interface, configure and deploy a Linux VM, and deploy the MRP system to the Linux VM.

Prerequisites
Before working on this hands-on lab, you must have:
- Azure Subscription (Free, Individual, or Organization)

Computers and Credentials
- Linux client (this HOL was written using Ubuntu 14.10)
- Internet Access
- The user should have Administrative rights on the local computer

## Exercise 1 - Prepare your environment ##

### Task 1.1 - Download files needed for lab ###
1. download the following files from <TODO: Source> to Downloads folder on your machine.
<pre><code>ProvisionAzureLinuxVM.sh from <TODO: Source>
Install-MRP.sh from <TODO: Soure>
MongoRecords.js from <TODO: Source>
ordering-service-0.1.0.jar from <TODO: Source>
mrp.war from <TODO: Source>

### Task 1.2 - Create an Azure Storage Account & Cloud Service ###
An Azure Storage account with a Blob container named “drops” will be used to upload and store files used by the provisioning script.  You will use the Azure Management Portal to create a storage account and Blob container. The Cloud Service will “own”  the provisioned resources that will be created when the provisioning script is run later in this exercise. 
>NOTE
>This task assumes you have created an Azure Subscription prior to starting the lab exercise.

1. Navigate to http://azure.microsoft.com using your browser.  
2. To log into the Management Portal, click **Portal**.
3. Enter the email address / Microsoft Account for your Azure subscription, click **Continue**.
4. Sign in using your password
5. On the portal page, to add a storage account, on the left navigation pane, click **Storage**.
6. On the Storage page, click **New**. Then, on the pop up dialog, click **Quick Create**.
Enter the URL, select a data center location (e.g., East US, West US), selecting Geo-Redundant for Replication. Click the check mark at the bottom of the dialog to finish.
>Note, the URL must be unique, all lower case letters or numbers with 3-24 characters.

7. You will be on the storage page when the operation completes
8. On the storage page, to add a Blob container, click the name of the storage account you just created. On the page with the title of your storage account, click **Containers** on the top menu.
9. On the Container page, click **add** at the bottom of the screen.
10. In the pop up dialog, enter a name: "drops" and select Public Blob from the Assess list. Click on the check box to finish.

>Note: The name should be between 3 and 63 characters log, only letters, numbers, and hyphens, and must begin with a letter or a number. The name cannot contain two consecutive hyphens.

11. To create a cloud service, click on Cloud Service on the left hand navigation panel
12. Click on the right arrow link (Create a cloud service> on the Cloud Service page.
13. On the pop up dialog, enter a valid cloud service name. Click the check mark to finish.

>Note: the name field can only contain only letters, numbers, and hyphens. The first and last character in the field must be a letter or number. Trademarks, reserved words, and offensive words are not allowed. 


### Task 1.3 - Installing Node.js and xplat-cli ### 
**GOAL** In this exercise, you will install Node.js to gain access to the Node.JS Package Manager and the Azure xplat-cli.
<p>Begin this task by opening up a Terminal window in your Linux environment.<p>

1. Type `sudo apt-get install nodejs-legacy` and press Enter
2. Type `sudo apt-get install npm` and press Enter
3. Type `sudo npm install -g azure-cli` and press Enter


## Exercise 2 - Connect to your Azure subscription ##

**GOAL** In this exercise, you will download your Azure subscription setting file which will be used later in this lab.

1.	Download your Azure credentials into the **publishsetting** file using the following command:
<pre><code>azure account download</code></pre>
Upon execution, your default browser should open and you will enter in your credentials to connect to your Azure subscription.

>NOTE: If it doesn't, look at the Terminal window for the URL it was trying to launch and paste the URL in the browser.

>If your Microsoft ID is associated with a personal account as well as your organization account, you will need to decide which on to use. If you are using your Azure Subscription from MSDN, select your personal account.

2. Download and save the **publishsetting** file. Make note of where this file is saved.

>NOTE: the filename is long so it might be a good idea to shorten it a bit to make it easier to work with.


## Exercise 3 - Configure the Shell Script to Provision Linux Environment and Deploy the MRP Application ##
**GOAL** In this exercise, you will make a number of configuration settings to create an environment to host Linux VMs and deploy the MRP application to.
### Task 3.1 - Configure the Shell Script for your Azure environment ###
1. Open the **ProvisoinLinuxVM-xplat-cli.sh** in your text editor (e.g., gedit or VI)
2. At the top of the script, there are a number of global variables you need to populate before you run the script.

|Variable|Description|
|-|-|
|PublishSettingsFileLocation | This the location and filename of the **publishsetting** file you downloaded in **Excercise 2, Step 1**. If you file has spaces in it, you should rename it something shorter and with no spaces. |
|AzureLocation|What data center do you want use. You see what locations are available using `azure site location list`|
|StorageAccountName|Name of the Storage Account. **NOTE:** it should be in lowercase|
|ContainerName|Name of the Azure Container|
|VirtualNetwork|Name of the Virtual Network that will be created in which the Linux VMs will communicate with one another|
|VSubNet|Name of the Virtual Subnet they will be using|
|LocalAdmin|Name of the Local Admin account for the Linux VMs (e.g., labadmin)|
|LocalPass|Local Admin account's password. **NOTE:** it needs to be at least 8 alphanumeric characters long with at least a special character like !, $, or %|
|LinuxVMName|Base name of the Linux VM|
|MRPInstallScriptName|Name of the MRP installation script|
|DropStorageAccount|Name of the Drop Storage Account|
|DropContainerName|Name of the Drop Container|

3. Save the changes

### Task 3.2 - Provision the Azure VM and MRP app ###
1. Execute the Shell Script using `sudo bash ProvisionLinuxVM-xplat-cli.sh` and press Enter
2. The script will run for several minutes with its various stages of execution being echoed back to you.
3. Once it's done you have a fully provisions Linux environment with the MRP application deployed to it.
4. You can view the information of all components configured and built in the Azure Management Portal [link text](http://manage.windowsazure.com)

### Task 3.3 -- Confirm the VM is Running ###
GOAL To confirm that the VM is running, you should SSH to the VM (this lab assumes you have PuTTy installed on your machine).

1.	Get and note the IP Address of the provision VM using the following command:
<pre><code>azure vm show &lt;&lt;LinuxVMName&gt;&gt;</code></pre>

>Note: <<LinuxVMName>> is the name used in Task 3.2 Step 2.

2.	Start the PuTTy application
3.	Enter the IP Address From the Step 1 of this Task for the Host Name.
4.	Enter the login credentials you entered into the Provisioning file in Task 3.2 Step 2.
5.	Log into the VM.

## For more information ##
Install and Configure the Azure Cross-Platform Command-Line Interface
[link text](http://azure.microsoft.com/documentation/articles/xplat-cli/)

Using the Azure Cross-Platform Command-Line Interface
[link text](http://azure.microsoft.com/documentation/articles/virtual-machines-command-line-tools/)

Cross-Platform Cloud Automation with JavaScript
[link text](https://msdn.microsoft.com/magazine/dn890376.aspx)

Quick Start Guide: Building Highly Available Linux Servers in the Cloud on Microsoft Azure in 12 Steps
[link text](http://blogs.technet.com/b/keithmayer/archive/2014/10/03/quick-start-guide-building-highly-available-linux-servers-in-the-cloud-on-microsoft-azure.aspx)
