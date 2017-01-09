# Deploying the Parts Unlimited MRP App via Puppet in Azure
In this hands-on lab, you will deploy a Java app, the Parts Unlimited MRP App, using Puppet from [PuppetLabs](https://puppetlabs.com/). Puppet is a configuration
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
1. Install Puppet Agent on the node
1. Configure the Puppet Production Environment
1. Test the Production Environment Configuration
1. Create a Puppet program to describe the environment for the MRP application

## Task 1: Provision the Lab

1. Provision the Lab machines using an Azure Resource Manager (ARM) Template

    This lab calls for the use of two machines. The Puppet Master server must be a Linux machine, but the puppet
    agent can run on Linux or Windows. For this lab, the _node_ that we will be configuring is an Ubuntu VM.

    Instead of manually creating the VMs in Azure, we are going to use an Azure Resource Management (ARM) template.

    The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
    delete the resource group in order to remove all the created resources at any time.

    ![](<media/1.jpg>)

    ```
    partspuppetmaster.nqkkrckzqwwu1p5pu4ntvzrona.cx.internal.cloudapp.net
    ```
