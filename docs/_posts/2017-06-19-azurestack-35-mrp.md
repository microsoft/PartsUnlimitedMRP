---
layout: page
title:  Getting Started with Parts Unlimited MRP
category: AzureStack
order: 5
---
As mentioned in the previous steps, Parts Unlimited MRP is a fictional outsourced Manufacturing Resource Planning (MRP) application for training purposes based on the description in chapters 31-35 of The Phoenix Projectby Gene Kim, Kevin Behr and George Spafford. © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

The application uses entirely open source software including Linux, Java, Apache, and MongoDB which creates a web front end, an order service, and an integration service.

As part of the deployment, you'll have the opportunity to embrace a number of core DevOps practices, along with experiencing a number of different open source technologies.

#### Key Features
- Front end service - runs Apache Tomcat and talks to order service
- Order and Integration service - runs Java and calls MongoDB
- Integration service - present to integrate with Parts Unlimited Website
- Includes a Dockerfile and sample publishing profile to publish to a Docker container
- Includes Azure RM JSON templates and PowerShell automation scripts to easily build and provision your environment

#### Fundamentals
- Infrastructure as Code - (IaC)
- Continuous Integration - (CI)
- Continuous Deployment - (CD)
- Configuration Management - (CM)

As time moves forward, more documents will be added that support additonal DevOps practices, going beyond the fundamentals and exploring more advanced techniques and tools.

### Choose a path
Below, you'll find the current scenarios, with a brief description. Some will consume more time than others, however all of them will provide valuable learnings across core DevOps practices.

#### Jenkins - Continuous Integration and Continuous Deployment
In this multi-part lab, we will set up the Jenkins Master in Azure Stack, that will be used for the Parts Unlimited MRP project. We'll then step through using that environment to showcase CI/CD capabilities. [Jenkins](https://jenkins.io/) is an open source automation server that provides capabilities for building, deploying and automating any project.

[Go to the Jenkins lab!](azurestack-36-jenkins-setup.html)

#### Puppet - Continuous Deployment with Puppet
In this multi-part lab, you will deploy a Java app, the Parts Unlimited MRP App, using [Puppet](https://puppet.com/) from PuppetLabs. Puppet is a configuration management system that allows you to automate provisioning and configuration of machines by describing the state of your infrastructure as code. Infrastructure as Code is an important pillar of good DevOps.

[Go to the Puppet lab!](azurestack-39-puppet-setup.html)

#### Chef - Continuous Deployment with Chef
In this multi-part lab, you will explore some of the new features and capabilities of deploying the Parts Unlimited MRP App via Chef Server in Azure Stack. This hands-on lab is designed to point out new features of [Chef Server](https://www.chef.io/), discuss and describe them, and enable you to understand and explain these features to customers as part of the DevOps Lifecycle.

[Go to the Chef lab!](azurestack-41-chef-setup.html)
