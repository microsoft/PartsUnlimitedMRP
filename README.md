# Parts Unlimited MRP#

Parts Unlimited MRP is a fictional outsourced Manufacturing Resource Planning (MRP) application for training purposes based on the description in chapters 31-35 of The Phoenix Projectby Gene Kim, Kevin Behr and George Spafford. © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

The application uses entirely open source software including Linux, Java, Apache, and MongoDB which creates a web front end, an order service, and an integration service. Click here for the related [Parts Unlimited Website application](http://github.com/microsoft/partsunlimited).

To read more about this project, please view the [docs folder](docs).

## Key Features##
- Front end service - runs Apache Tomcat and talks to order service
- Order and Integration service - runs Java and calls MongoDB
- Integration service - present to integrate with Parts Unlimited Website
- Includes a Dockerfile and sample publishing profile to publish to a Docker container
- Includes Azure RM JSON templates and PowerShell automation scripts to easily build and provision your environment