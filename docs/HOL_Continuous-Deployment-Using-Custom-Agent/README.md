# HOL - Parts Unlimited MRP App Continuous Deployment with Visual Studio Team Services #

In this lab you will learn how to deploy the Parts Unlimited MRP App in an automated fashion. After this lab, you will have a working, automated build in Visual Studio Online that will build, test, and deploy the Parts Unlimited MRP app to a Virtual Machine in Azure.


###Pre-requisites###

- HOL Continuous Integration with Parts Unlimited MRP
- Active Azure Subscription
- Active Visual Studio Online Account


## Tasks

In this lab you will work with one machine which will serve as both the deployment agent and the MRP server.

1. Provisioning a VSTS agent and MRP machine (Ubuntu VM) in Azure using an ARM template
1. Configure release definition
1. Trigger build for continuous deployment

## Task 1: Provision the Lab

1. Provision the Lab machines using an Azure Resource Manager (ARM) Template

    Instead of manually creating the VM in Azure, we are going to use an Azure Resource Management (ARM) template.
    
1. Click on the "Deploy to Azure" button
    
    Simply click the Deploy to Azure button below and follow the wizard to deploy the machine. You will need
    to log in to the Azure Portal.
                                                                     
    <a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fnwcadence%2FPartsUnlimitedMRP%2FHOL_Continuous-Deployment%2Fdocs%2FHOL_Continuous-Deployment%2Fenv%2FContinuousDeploymentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://azuredeploy.net/deploybutton.png"/>
    </a>
    <a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2Fnwcadence%2FPartsUnlimitedMRP%2FHOL_Continuous-Deployment%2Fdocs%2FHOL_Continuous-Deployment%2Fenv%2FContinuousDeploymentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://armviz.io/visualizebutton.png"/>
    </a>

    The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
    delete the resource group in order to remove all the created resources at any time.

1. Specify settings for the deployment
    
    You will need to select a subscription and region to deploy the Resource Group to and to supply an admin username 
    and password and unique name for both machines. The will be a Standard A2.

    ![](<media/1.jpg>)

    Make sure you make a note of the region as well as the usernames and passwords for the machines. Allow
    about 10 minutes for deployment and then another 10 minutes for the VSTS agent and MRP dependency configuration. 

1. Check the Resource Group in the Azure Portal
    When the deployment completes, you should see the following resources in the Azure Portal:

    ![](<media/2.jpg>)

    Click on the "partsmrp" Public IP Address. Then make a note of the DNS name:

    ![](<media/4.jpg>)

    >**Note:** The lab requires several ports to be open, such as SSH ports and the Parts Unlimited MRP app port on the partsmrp machine. 
	The ARM template opens these ports on the machine for you.

1. Log in to the Puppet Console

    Now go back to the Puppet Console in your browser and enter the username `admin` and the password you set. 
    When you log in, you should see a page like this:

    ![](<media/6.jpg>)

## Task 2: Create release definition


## Task 3: Continuous Deployment