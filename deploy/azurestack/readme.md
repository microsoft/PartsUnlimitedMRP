# What is Azure Stack?

Microsoft Azure Stack is a new hybrid cloud platform product that enables you to deliver Azure services from your own datacenter.

Azure Stack gives you the power of cloud services, yet enables IT to maintain control of your datacenter for true hybrid cloud agility. You decide where to keep your data and applications—in your own datacenter or with a hosting service provider. Easily access public cloud resources to scale at busy times, for dev-test, or whenever you need them.

## Deliver Azure services to your datacenter

Azure Stack enabled you to transform on-premises datacenter resources into cloud services for maximum agility. Run Azure IaaS services—including Virtual Machines, Blob/Table storage, and Docker-integrated Linux containers—for applications like SQL Server or SharePoint. Empower developers to write cloud-first applications using on-premises deployments of Azure PaaS services such as App Service. Make your application counterparts productive by enabling the same self-service experience as Azure.

You can learn more about Azure Stack on the dedicated [Azure Stack website](https://azure.microsoft.com/en-us/overview/azure-stack/) and see some cool videos on the [Azure Stack Channel on Channel 9](https://channel9.msdn.com/Blogs/azurestack)

It's also a great platform to learn about DevOps, and what better way to learn about DevOps, than through utilizing the Parts Unlimited MRP solution.

# Parts Unlimited MRP

Parts Unlimited MRP is a fictional outsourced Manufacturing Resource Planning (MRP) application for training purposes based on the description in chapters 31-35 of The Phoenix Projectby Gene Kim, Kevin Behr and George Spafford. © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

The application uses entirely open source software including Linux, Java, Apache, and MongoDB which creates a web front end, an order service, and an integration service. Click here for the related [Parts Unlimited Website application](http://github.com/microsoft/partsunlimited).

To read and learn more about this project, please visit the [documentation website](https://aka.ms/pumrplabs).

## Key Features
- Front end service - runs Apache Tomcat and talks to order service
- Order and Integration service - runs Java and calls MongoDB
- Integration service - present to integrate with Parts Unlimited Website
- Includes a Dockerfile and sample publishing profile to publish to a Docker container
- Includes Azure RM JSON templates and PowerShell automation scripts to easily build and provision your environment

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

# Getting Started with DevOps on Azure Stack

Just like for the Parts Unlimited MRP on Azure documentation, this detailed series of documents will walk you through various deployments and configurations, along with usage of a number of core DevOps tools such as Chef, Puppet, Jenkins and more, all on Azure Stack.

Interested? Head on over to the [getting started page](/deploy/azurestack/docs/readme.md) to start kicking the tires.
