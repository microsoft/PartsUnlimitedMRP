---
layout: page
title:  Connecting to Azure Stack
category: AzureStack
order: 2
---

# Connecting to Azure Stack

With your Azure Stack deployed, you should be logged in to the Azure Stack POC host, ready to use the environment. Now, in order to use the environment correctly, you will need to log into the MAS-CON01 virtual machine. This machine is your primary machine for administering, deploying and managing the Azure Stack.

## Connect with Remote Desktop
With a Remote Desktop connection, up to two concurrent (different) users can work with the portal to manage resources. You can also use tools on the MAS-CON01 virtual machine.

1. Log in to the Azure Stack POC physical machine.
2. Open a Remote Desktop Connection and connect to MAS-CON01. Enter **AzureStack\AzureStackAdmin** as the username, and the administrative password you provided during Azure Stack setup.  
3. On the MAS-CON01 desktop, open Server Manager, click **Local Server**, turn off Internet Explorer Enhanced Security, and then close Server Manager.
4. Double-click **Microsoft Azure Stack Admin Portal** icon (https://adminportal.local.azurestack.external/) to open the portal
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

# Next Step

In the next step, we'll [add a new VM image](/deploy/azurestack/docs/2017-06-19-azurestack-33-images.md), that we'll use for our ongoing tasks to evaluate key DevOps tooling on Azure Stack.
