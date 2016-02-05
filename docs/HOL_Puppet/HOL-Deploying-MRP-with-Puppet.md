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
The Puppet Master VM comes from the Azure MarketPlace. It is configured to install Puppet Server from an answerfile.
One of the fields in the answerfile is the admin password. We will now SSH into the Puppet Master and retrieve the
password. 

## Task 3: Install Puppet Agent on the node

## Task 4: Create a Puppet Program to Configure the MRP Node
The Parts Unlimited MRP application is a Java application that requires [mongodb](https://www.mongodb.org/)
and [tomcat](http://tomcat.apache.org/).