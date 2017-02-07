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

## Deploy a Virtual Machine

In order to test the Azure Stack functionality, a simple test would be to deploy a virtual machine.

1. From inside the portal, click **New**, and then **Virtual Machines**.
2. In the Virtual Machines blade, click **See all**.
3. In the new Virtual Machines blade, you'll be presented with all of the choices you can make, for deployment within your Azure Stack. As it stands, by default, there is only a standalone Windows Server 2012 R2 virtual machine, or an Availability Set. Select **WindowsServer-2012-R2-Datacenter** then click **Create**.
4. In the Create Virtual Machine blade, provide the basic information and click **OK**.

![Enter Basic Info](/deploy/azurestack/docs/media/basicinfo.png)
5. Choose a virtual machine size for your deployment. It's recommended just to go with the default **A2 standard**, then click **Select**.
6. Configure any optional features you wish to change. For the purposes of this test, simply accept the defaults and click **OK**.
7. Review the final summary, wait for validation, and then click **OK** to start the deployment.

This deployment may take a few minutes, and will depend on your hardware. Once complete, you should see a new tile on your dashboard.

