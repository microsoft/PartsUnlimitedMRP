# Deploying the Parts Unlimited MRP App via Puppet in Azure
In this hands-on lab you will deploy a Java app, the Parts Unlimited MRP App, using Puppet from [PuppetLabs](https://puppetlabs.com/). Puppet is a configuration
management system that allows you to automate provisioning and configuration of machines by describing the state of your infrastructure
as code. Infrastructure as Code is an important pillar of good DevOps.

## Prerequisites
- An SSH client such as PuTTY
- An Azure subscription

## Tasks

In this lab you will work with two machines: a Puppet Master machine and another machine known as a _node_
which will host the MRP application. The only task you will perform on the node is to install the Puppet
agent - the rest of the configuration will be applied by instructing Puppet how to configure the node 
though _puppet programs_ on the Puppet Master. 

1. Provisioning a Puppet Master and node (both Ubuntu VMs) in Azure using ARM templates
1. Retrieve the Puppet Master admin password
1. Install Puppet Agent on the node
1. Create a puppet program to describe the environment for the MRP application
1. Manually triggering the provisioning
1. Remediating Configuration Changes

## Task 1: Provision the Lab
This lab calls for the use of two machines. The Puppet Master server must be a Linux machine, but the puppet
agent can run on Linux or Windows. For this lab, the _node_ that we will be configuring is an Ubuntu VM.

Instead of manually creating the VMs in Azure, we are going to use an Azure Resource Management (ARM) template.
Simply click the Deploy to Azure button below and follow the wizard to deploy the two machines. You will need
to log in to the Azure Portal.

The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
delete the resource group in order to remove all the created resources at any time.

You will need to select a subscription and region to deploy the Resource Group to and to supply an admin username 
and password and unique name for both machines. For simplicity, both machines will be created using Standard A2 size.

Make sure you make a note of the region as well as the usernames and passwords for the machines. Allow
about 10 minutes for deployment and then another 10 minutes for the Puppet Master to configure Puppet. 

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fcolindembovsky%2FPartsUnlimitedMRP%2Fpuppet%2Fdocs%2FHOL_Puppet%2Fenv%2FPuppetPartsUnlimitedMRP.json" target="_blank">
    <img src="http://azuredeploy.net/deploybutton.png"/>
</a>
<a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2Fcolindembovsky%2FPartsUnlimitedMRP%2Fpuppet%2Fdocs%2FHOL_Puppet%2Fenv%2FPuppetPartsUnlimitedMRP.json" target="_blank">
    <img src="http://armviz.io/visualizebutton.png"/>
</a>

![](<media/1.jpg>)

When the deployment completes, you should see the following resources in the Azure Portal:

![](<media/2.jpg>)

Click on the "partspuppetmaster" Public IP Address. Then make a note of the DNS name:

![](<media/3.jpg>)

The _dnsaddres_ will be of the form _machinename_._region_.cloudapp.azure.com. Open a browser to https://_dnsaddress_.
(Make sure you're going to http__s__, not http). You will be prompted about an invalid certificate - it is safe to
ignore this for the purposes of this lab. If the Puppet configuration has succeeded, you should see the Puppet Console
sign in page:

![](<media/4.jpg>)

## Task 2: Retrieve the Puppet Master admin password
The Puppet Master VM is created from an image that PuppetLabs maintiains in the Azure MarketPlace. When a VM is created 
from the image, it installs and configures Puppet Server from an answerfile. One of the fields in the answerfile is the 
admin password. We will now SSH into the Puppet Master and retrieve the password.

SSH into the puppet master VM by opening PuTTY or some other SSH console and connecting to _dnsaddress_ as noted above.
Enter in the username and password that you configured when creating the resource group earlier. Once you have logged
in, enter the following command:

```sh
sudo cat /etc/puppetlabs/installer/database_info.install
```

![](<media/5.jpg>)

Make a note of the password with the key `q_puppet_enterpriseconsole_auth_password`. Then go back to the Puppet Console
in your browser and enter the username `admin` and the password you just retrieved. When you log in, you should see
a page like this:

![](<media/6.jpg>)

>**Note:** It is recommended that you change the admin password. In the toolbar, click 'admin->My Account' and then click
the "Reset Password" link in the upper right:
![](<media/7.jpg>)

In the SSH console, enter the following command to make a note of the internal IP address of the Pupper Master:

```
hostname -i
```

The IP address should be something like `10.0.0.4`.

## Task 3: Install Puppet Agent on the node
You are now ready to add the node to the Puppet Master. Once the node is added, the Puppet Master will be able to configure
the node.

Click on the "No Node Requests" button in the toolbar at the top of the console. The page that loads will show a command
that we need to run on the node. Make a note of the machine name (the internal VNet name of the puppet master, since we 
will have to add this address into the hosts file on the node so that the node will be able to resolve the IP address of 
the puppet master. In the example below, the puppet master machine name is:

```
partspuppetmaster.nqkkrckzqwwu1p5pu4ntvzrona.cx.internal.cloudapp.net
```
![](<media/8.jpg>)

Now open another SSH terminal and this time log into the node using the 2nd username and password that you selected when
the resource group was created.

Once you have logged in, enter the following command to edit the hosts file:

```
sudo nano /etc/hosts
```

Enter a new line on line 2 with the following:

_IP-of-puppet-master_ puppetmaster _internal-name-of-puppet-master_. For example:
```
10.0.0.4 partspuppetmaster partspuppetmaster.nqkkrckzqwwu1p5pu4ntvzrona.cx.internal.cloudapp.net
```
![](<media/9.jpg>)


Then press cntrl-X to exit, and enter 'Y' to save the changes, and enter to confirm the file name. Test your edits by
entering the following command:
```
ping partspuppetmaster.nqkkrckzqwwu1p5pu4ntvzrona.cx.internal.cloudapp.net
```

You should see a reply from the ping. Press cntrl-C to stop the ping.

![](<media/10.jpg>)

Next, enter the command that you copied from the Puppet Console to add the node:
![](<media/11.jpg>)

The command will take a few moments to complete.

When the command is complete, you can exit the SSH terminal. From here on, you will configure the node only from
the Puppet Master.

Return to the Puppet Console and refresh the node requests page (where you previously go the node install command). You
should see a pending request. This request has come from the node and will authorize the certificate between the puppet
master and the node so that they can communicate securely. Press "Accept" to approve the node:
![](<media/12.jpg>)

Click on the "Nodes" tab in the Puppet Console to return to the nodes view. You should see 2 nodes listed: the puppet
master and the partsmrp node:

![](<media/13.jpg>)

## Task 4: Create a Puppet Program to Configure the MRP Node
The Parts Unlimited MRP application is a Java application that requires [mongodb](https://www.mongodb.org/)
and [tomcat](http://tomcat.apache.org/) to be installed and configured on the partsmrp machine (the node). Instead of
installing and configuring manually, we will now write a puppet program that will instruct the node how to configure
itself.

Puppet Programs are stored in a particular folder in the puppet master. Puppet programs are made up of manifests
that describe the desired state of the node(s). The manifests can consume modules, which are pre-packaged Puppet
Programs. Users can create their own modules or consume modules from a marketplace maintained by PuppetLabs knows
as the Forge. Some modules on the Forge are official modules that are supported - others are open-source modules
uploaded from the community.

There are two major ways to organise Puppet Programs. One is by _site_, and the other is by _environment_. Organizing
by site allows you to configure a group of nodes from a single catalog. Howeverm, it is better to organize by 
environment, allowing you to manage different catalogs for different environments such as dev, test and production.

For the purposes of this lab, we will treat the node as if it were in the production environment. We will also need to 
download a few modules from the Forge which we will consume to configure the node.

When the Puppet Server was installed in Azure, it configured a folder for managing the production environment
in the following path:

```
/etc/puppetlabs/puppet/environment/production
```

On the SSH terminal to the Puppet Master, cd to that folder now:

```
cd /etc/puppetlabs/puppet/environment/production
```

If you run `ls` you will see two folders: `manifests` and `modules`.