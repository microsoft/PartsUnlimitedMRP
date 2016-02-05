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
1. Install the Puppet Agent on the node
1. Create a puppet program to describe the environment for the MRP application
1. Manually triggering the provisioning
1. Remediating Configuration Changes

## Task 1: Provision the Lab
This lab calls for the use of two machines. The Puppet Master server must be a Linux machine, but the puppet
agent can run on Linux or Windows. For this lab, the _node_ that we will be configuring is an Ubuntu VM.

Instead of manually creating the VMs in Azure, we are going to use an Azure Resource Management (ARM) template.
Simply click the Deploy to Azure button below and follow the wizard to deploy the two machines.

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fcolindembovsky%2FPartsUnlimitedMRP%2Fpuppet%2Fdocs%2FHOL_Puppet%2Fenv%2FPuppetPartsUnlimitedMRP.json" target="_blank">
    <img src="http://azuredeploy.net/deploybutton.png"/>
</a>
<a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2Fcolindembovsky%2FPartsUnlimitedMRP%2Fpuppet%2Fdocs%2FHOL_Puppet%2Fenv%2FPuppetPartsUnlimitedMRP.json" target="_blank">
    <img src="http://armviz.io/visualizebutton.png"/>
</a>


## Task 2: Create a Puppet Program to Configure the MRP Node
The Parts Unlimited MRP application is a Java application that requires [mongodb](https://www.mongodb.org/)
and [tomcat](http://tomcat.apache.org/).