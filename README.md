# Parts Unlimited MRP #

Parts Unlimited MRP is a fictional outsourced Manufacturing Resource Planning (MRP) application for training purposes based on the description in chapters 31-35 of The Phoenix Projectby Gene Kim, Kevin Behr and George Spafford. © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

The application uses entirely open source software including Linux, Java, Apache, and MongoDB which creates a web front end, an order service, and an integration service. 

The master branch of this repo contains the PartsUnlimtedMRP application, and deployment and configuration files of that sample app. This sample app and configuration files are then used in the labs appearing on the github pages site <a href="http://microsoft.github.io/PartsUnlimitedMRP" target="_blank"><span style="color: #0066cc;" color="#0066cc">http://microsoft.github.io/PartsUnlimitedMRP</span></a>. A shortend url is available in the form of <a href="http://aka.ms/pumrplabs" target="_blank"><span style="color: #0066cc;" color="#0066cc">http://aka.ms/pumrplabs</span></a>

Any updates or changes to the application or labfiles can be made be merging changes into this **master** branch, but lab steps and graphics are sourced from the **gh-pages** branch. Updates to lab page configuration, the lab steps or graphics can be done through the <a href="https://github.com/Microsoft/PartsUnlimitedMRP/tree/gh-pages" target="_blank"><span style="color: #0066cc;" color="#0066cc">https://github.com/Microsoft/PartsUnlimitedMRP/tree/gh-pages</span></a> branch of this repo.

To get started, please proceed to the page <a href="http://microsoft.github.io/PartsUnlimitedMRP" target="_blank"><span style="color: #0066cc;" color="#0066cc">http://microsoft.github.io/PartsUnlimitedMRP</span></a>



## PartsUnlimited MRP  - Key Features
- Front end service - runs Apache Tomcat and talks to order service
- Order and Integration service - runs Java and calls MongoDB
- Integration service - present to integrate with Parts Unlimited Website
- Includes a Dockerfile and sample publishing profile to publish to a Docker container
- Includes Azure RM JSON templates and PowerShell automation scripts to easily build and provision your environment

For the labs based around the PartsUnlimited .NET application see the page <a href="http://microsoft.github.io/PartsUnlimited" target="_blank"><span style="color: #0066cc;" color="#0066cc">http://microsoft.github.io/PartsUnlimited</span></a>. It will show you how to use Visual Studio Team Services with a .NET application and attempts to use exclusively Microsoft products, tools and services, or 3rd party and open source products integrating with Microsoft Products and services.

## Microsoft Professional Program (MPP) for DevOps

These labs are used as part of the **Microsoft Professional Program (MPP) with DevOps** series of online courses. The successful completion of the courses and capstone project, that together make up the   **MPP for DevOps**, results in the granting of the **Microsoft MPP for DevOps** credential. For more information on the **Microsoft Professional Program (MPP) for DevOps** program see the pages <a href="https://academy.microsoft.com/en-us/professional-program/tracks/devops/ " target="_blank"><span style="color: #0066cc;" color="#0066cc">https://academy.microsoft.com/en-us/professional-program/tracks/devops/ </span></a> and <a href="https://www.edx.org/microsoft-professional-program-devops " target="_blank"><span style="color: #0066cc;" color="#0066cc">https://www.edx.org/microsoft-professional-program-devops</span></a> 



This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.