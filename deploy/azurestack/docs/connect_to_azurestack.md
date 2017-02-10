# Connecting to Azure Stack#

With your Azure Stack deployed, you should be logged in to the Azure Stack POC host, ready to use the environment. Now, in order to use the environment correctly, you will need to log into the MAS-CON01 virtual machine. This machine is your primary machine for administering, deploying and managing the Azure Stack.

## Connect with Remote Desktop
With a Remote Desktop connection, a single concurrent user can work with the portal to manage resources. You can also use tools on the MAS-CON01 virtual machine.

1. Log in to the Azure Stack POC physical machine.
2. Open a Remote Desktop Connection and connect to MAS-CON01. Enter **AzureStack\AzureStackAdmin** as the username, and the administrative password you provided during Azure Stack setup.  
3. On the MAS-CON01 desktop, open Server Manager, click **Local Server**, turn off Internet Explorer Enhanced Security, and then close Server Manager.
4. Double-click **Microsoft Azure Stack Portal** icon (https://portal.azurestack.local/) to open the portal
5. Log in using the Azure Active Directory credentials specified during installation.

Once you've successfully connected, you're free to explore the Azure Stack environment.

## Connect with a VPN
Virtual Private Network connections let multiple concurrent users connect from clients outside of the Azure Stack infrastructure. You can use the portal to manage resoures. You can also use tools, such as Visual Studio and PowerShell, on your local client. If you require a VPN connection to connect to your Azure Stack, please [refer to the documentation](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-connect-azure-stack).

## Update Internet Explorer Enhanced Security Configuration

It's easier if you do this now, rather than later. It'll make things easier when accessing Azure Stack both programmatically, and via the Portal.

1. On MAS-CON01, open **Server Manager**, then click **Local Server**.
2. On the right-hand side, next to IE Enhanced Security Configuration, click **On**.
3. In the Internet Explorer Enhanced Security Configuration window, for Administrators, select **Off** and click **OK**.
4. Close Server Manager.

## Deploy a Virtual Machine

In order to test the Azure Stack functionality, a simple test would be to deploy a virtual machine.

1. From inside the portal, click **New**, and then **Virtual Machines**.
2. In the Virtual Machines blade, click **See all**.
3. In the new Virtual Machines blade, you'll be presented with all of the choices you can make, for deployment within your Azure Stack. As it stands, by default, there is only a standalone Windows Server 2012 R2 virtual machine, or an Availability Set. Select **WindowsServer-2012-R2-Datacenter** then click **Create**.
4. In the Create Virtual Machine blade, provide the basic information and click **OK**.

    ![Enter Basic Info](/deploy/azurestack/docs/media/CreateVMBasic.PNG)
5. Choose a virtual machine size for your deployment. It's recommended just to go with the default **A2 standard**, then click **Select**.
6. Configure any optional features you wish to change. For the purposes of this test, simply accept the defaults and click **OK**.
7. Review the final summary, wait for validation, and then click **OK** to start the deployment.

This deployment may take a few minutes, and will depend on your hardware. Once complete, you should see a new tile on your dashboard.

   ![VM Deployed](/deploy/azurestack/docs/media/VMdeployed.PNG)

Whilst it's great to have a Windows Server to deploy onto Azure Stack, the real power is when you start to provide your own, customized images that can be deployed within the environment. These could be standard base Linux distro images, or something more customized, to include apps and other features that are enabled during the deployment.

# Next Step

In the next step, we'll [add a new VM image](/deploy/azurestack/docs/adding_vm_images.md), that we'll use for our ongoing tasks to evaluate key DevOps tooling on Azure Stack.

## Optional - Clean Up VM Deployment

If you're not going to use the previously deployed Windows Server VM, follow these steps to remove it from your Azure Stack environment:

1. In the Azure Stack Portal, click **Resource Groups**
2. In the list of Resource Groups, find your entry that you created during the Windows Server VM deployment, and on the far-right of the list, click the 3 dots **...** then choose **Delete**.
3. In the new blade, enter the name of the Resource Group, and click **Delete**. This will clean up all of the artifacts from your deployment. It will take a few minutes, and you will receive a notification when complete.
