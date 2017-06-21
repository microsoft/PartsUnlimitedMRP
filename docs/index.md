---
layout: default
title: "PartsUnlimitedMRP"
---

# Parts Unlimited MRP

Parts Unlimited MRP is a fictional outsourced Manufacturing Resource Planning (MRP) application for training purposes based on the description in chapters 31-35 of The Phoenix Projectby Gene Kim, Kevin Behr and George Spafford. © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

The application uses entirely open source software including Linux, Java, Apache, and MongoDB which creates a web front end, an order service, and an integration service. Click here for the related [Parts Unlimited Website application](http://github.com/microsoft/partsunlimited).

To read more about this project, please visit the [documentation website](http://aka.ms/pumrplabs).

You may watch a [demo in Channel 9](https://channel9.msdn.com/Blogs/TalkDevOps/TalkDevOps--Deploying-a-Java-application-with-VSTS) that walks through many of the steps in the document.

## Key Features ##
- Front end service - runs Apache Tomcat and talks to order service
- Order and Integration service - runs Java and calls MongoDB
- Integration service - present to integrate with Parts Unlimited Website
- Includes a Dockerfile and sample publishing profile to publish to a Docker container
- Includes Azure RM JSON templates and PowerShell automation scripts to easily build and provision your environment

## Fundamentals
- Continuous Integration - C.I
- Continuous Deployment - C.D
- Infrastructure as Code - IaC
- Configuration Management - ConfMgmt

## Fundamentals on Azure Stack
Microsoft Azure Stack is a new hybrid cloud platform product that enables you to deliver Azure services from your own datacenter. Just like for the Parts Unlimited MRP on Azure documentation, this detailed series of documents will walk you through various deployments and configurations, along with usage of a number of core DevOps tools on Azure Stack, including:
- Adding custom images and marketplace items to Azure Stack
- Continuous Integration and Deployment with Jenkins on Azure Stack
- Continuous Deployment & Configuration Management with Puppet on Azure Stack
- Continuous Deployment & Cnfiguration Management with Chef on Azure Stack

## Advanced
- Microservices using Azure Container Service or Docker Swarm
- Automated Security
- Testing in production
- A/B Testing
- ...

## You want to contribute ?
Please let us know, we are looking for people motivated to help us !


### Legal
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). 
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
