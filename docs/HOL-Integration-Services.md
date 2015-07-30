<properties
   pageTitle="Parts Unlimited Integration Services"
   description="Hands on lab to setup two way communication between the Parts Unlimited MRP outsourced application and the PartsUnlimited ASP.NET vNext e-commerce website."
   authors="dtzar"/>

<!---
<tags
   ms.service="required"
   ms.devlang="may be required"
   ms.topic="article"
   ms.tgt_pltfrm="may be required"
   ms.workload="required"
   ms.date="mm/dd/yyyy"
   ms.author="Your MSFT alias or your full email address;semicolon separates two or more"/>
-->

# Parts Unlimited

Parts Unlimited is a fictional organization that sells car parts online. Their business relies on two key applications: a new ASP.NET vNext e-commerce website and a outsourced Java Manufacturing Resource Planning (MRP) application. The scenario and e-commerce site is based on the website described in chapters 31-35 of The Phoenix Project, by Gene Kim, Kevin Behr and George Spafford, © 2013 IT Revolution Press LLC, Portland, OR. Resemblance to “Project Unicorn” in the novel is intentional; resemblance to any real company is purely coincidental.

These applications have been built independently. This has introduced several business challenges:

* Inventory levels and lead times are stored in the MRP; therefore, customers are unable to see this information on the e-commerce site leading to unexpected ordering delays. It also impacts PartsUnlimited's ability to efficiently manage inventory by effectively encouraging customers to order out of stock items.
* New orders need to be manually added to the MRP system to be processed. This adds a delay in order processing and means PartsUnlimited does not have a full view of pending orders when starting new manufacturing runs.

In this hands on lab, you will deploy the new cloud-hosted ASP.NET vNext e-commerce application and the outsourced 'on premises' MRP Java application. You will then connect them using common cloud integration patterns. High-level steps include:

* Setup and preparation of applications within your environment
* Review the applications running standalone
* Setup integration services for outsourced application
* Setup integration services for e-commerce application
* Explore the integrated applications 

#Setup and Preparation

## Prerequisites

In order to build and deploy the two applications you will need to have the following components installed on your development machine:

* [Azure][azure_sub] subscription
* [Visual Studio 2015][VS2015]
* [Microsoft Web Platform installer][WPI]
* [Azure PowerShell][powershell_commandlets]
* [Azure Command Line Interface][cli]
* [7-Zip][7Zip] or another archive tool
* [Postman][postman] or another program to send REST API calls
* [Putty and PSCP][putty] or another SSH and SCP client

## Setup and preparation of applications within your environment

### Deploy the MRP application

To prepare the MRP application, follow the instructions [here][pu_mrp_deploy].

It is not necessary for the purposes of this lab to build or modify the Java application as pre-built versions and scripts are provided. However, if you are interested in customizing the Java application, see the tutorial [here][pu_mrp_build].

### Deploy the Parts Unlimited eCommerce Website

To prepare the Parts Unlimited eCommerce Website, firstly download the source code from [here][pu_web_started], and then follow the deployment instructions [here][pu_web_deploy]. 

### Prepare the Parts Unlimited website for integration

To prepare the e-commerce site:

1. Login to the [Azure Management Portal][portal]
2. **Navigate** to the new web app
3. Click on the **Configure** tab
4. Scroll down to **App Settings**
5. **Create** a new entry:
  * Name: `AzureWebJobsDashboard`
  * Value: `DefaultEndpointsProtocol=https;AccountName=<your storage account name>;AccountKey=<your storage account key>` 

	> This is the shared storage connection string which is used for communication between the outsourced application and the website hosted in Azure. This connection string also enables the Web Jobs dashboard (available at `https://<your web app>.scm.azurewebsites.net/azurejobs`). The account was provisioned as part of the deployment process and you can retrieve the key from the portal. 
6. Click **Save**

## Explore standalone applications

Now that the applications are deployed, it's time to review the two main challenges faced by Parts Unlimited: ensuring that the stock levels displayed to customers are up to date and the timely fulfillment of orders.
![Isolated applications][6]

1. In your web browser, **navigate** to your deployment of the e-commerce web site
2. Scroll down the page until you find the **product listings**
3. Click a **product** image to navigate to the details page

	> Under the price, the stock status and lead time is displayed. However, as this is managed by the outsourced MRP system, the information displayed on the e-commerce site needs to be manually maintained and updated. This often leads to the wrong information being shown to customers.
5. Click **Add to Cart**
5. On the cart page, click **Checkout**
6. On the login page, either follow the link to create a new account or use the default administrator login:
 * Username: `administrator@test.com`
 * Password: `YouShouldChangeThisPassword1!`
7. On the payment page, complete the form and click **Submit Order**
8. Once the checkout is complete, **navigate** to `/orders` page on your deployment of the MRP application

	> The new order is not present in the MRP system. Until the order is logged with the MRP system, it cannot be processed. This manual process leads to slower deliveries for their customers. 

As you can see, the experience using the two applications while they are running independently introduces challenges for both Parts Unlimited and their customers. Until these two applications are integrated and have two-way communication, Parts Unlimited users will continue to experience frustration through delayed order processing times and incorrect stock levels.

## Integration Part 1: Outsourced application (Java Service)

The Parts Unlimited MRP app is a outsourced application that has run key internal business processes for manufacturing and inventory for many years. In order to enable the display of inventory levels and lead times to users of the e-commerce website, we need to create a communication between the outsourced application and the cloud.

For the purposes of this lab, the MRP application is hosted in a Linux VM in Azure. However, in the Parts Unlimited scenario, it's an application which would most likely be hosted on premises. Therefore our integration approach needs to accommodate this hosting arrangement.

### Review

Parts Unlimited have built an integration service that will run alongside the existing MRP app (on premises). It will use the existing REST APIs to retrieve data and communicate with the e-commerce site via Azure Queues. This avoids having to open publicly accessible inbound network ports (mitigating security concerns) and introduces a durable buffered connection to avoid overloading the MRP during periods of high demand or maintenance.

![Communicating Inventory][8]

Another option while still making use of underlying queues would be to use Service Bus over Azure Queues. Service Bus adds extra features which went above an beyond the requirements for the communication requirements. E.g. Transaction support and Delivery guarantees.  To compare and contrast the differences between these two options please review the documentation [here][AzureQueueVsServiceBus]

An alternative communication method to shared queues would be to publicly expose the MRP app's REST API; however, its security model did not consider this possibly when the application was built and therefore lacks any modern security options like TLS or OAuth. While it's possible to add these, MRP - like many outsourced apps - lacks a solid suite of unit tests and developer knowledge so instead Parts Unlimited would prefer to avoid code changes.

Also, the MRP application runs on a single VM. This means performance is constrained - especially when compared to the e-commerce app - and any solution will need to accommodate outages for patching and maintenance.

The integration service is a simple Java Spring app that runs side-by-side with the MRP app. The integration code runs as scheduled tasks:

1. Open **File Explorer** and navigate to the `$/src/Backend/IntegrationService/src/main/java/integration/scheduled` directory in your local PartsUnlimitedMRP repository
2. Open `UpdateProductProcessTask.java`

	> This task is configured to run on a fixed schedule (see the `@Scheduled` annotation). Each time it runs, it queries the MRP REST API to retrieve the product catalog with stock levels and pushes this information to the Azure 'product' queue. 
3. Open `CreateOrderProcessTask.java`

	> This task is also configured to run on a fixed schedule. Each time it runs, it retrieves new orders from the Azure 'orders' queue and inserts these into the MRP app via the REST API.

These two tasks establish the basics of two-way communication between the MRP app and the e-commerce app.

### Deploy

In order to deploy the integration service, it needs to be configured with the correct storage account:

1. Retrieve the **storage account name and access key** deployed as part of the MRP app

	> This information can be retrieved from the Azure Portal, via the CLI, or via PowerShell.
2. Open **File Explorer** and browse to `$/builds` directory in your local PartsUnlimitedMRP repository
3. Open `integration-service-0.1.0.jar` with **7-Zip** (or the ZIP program of your choice)
4. Right click the `application.properties` file and select **Edit**
5. Update the `azure.storage.connectionstring` property with an updated connection string in the following format: `DefaultEndpointsProtocol=https;AccountName=<your storage account name>;AccountKey=<your storage account key>`
6. **Save** the file
7. **Close** the editor
8. Return to **7-Zip** and press **OK** to update the file in the archive

Now that the integration service configuration has been updated, it can be deployed to the Linux VM:

1. From a **command prompt**, navigate to the `$/builds` directory in your local PartsUnlimitedMRP repository
2. **Run** the secure copy command using `pscp` or your client of choice: `pscp -P <your Linux VM SSH port> ./integration-service-0.1.0.jar mrp_admin@<your Linux VM DNS name>.cloudapp.net:~`

	> The service DNS name and SSH port can be retrieved from the Azure Portal, via the CLI, or via PowerShell. The password is contained in the provisioning script used to initially provision the environment (default: `P2ssw0rd`).
3. **Wait** for the upload to complete.
4. **Start** the `putty` application (or your SSH client of choice)
5. **Connect** to `<your Linux VM DNS name>.cloudapp.net` on port `<your Linux VM SSH port>`.
6. **Run** the integration service with the command: `java -jar integration-service-0.1.0.jar &`

	> The ampersand forces the command to run in the background (and remain running once the user logs off).
7. The application will now start and output some information to the console
8. Once complete, **disconnect** with the command: `logout`

### Test

The `UpdateProductProcessTask` task runs every thirty seconds (by default). If the integration service is running normally, the first message should appear in the `product` queue after this period of time:

1. Open Visual Studio 2015
2. Expand the **Azure** node in the tree (login if required)
3. Expand the **Storage Accounts** node
4. Expand the **PartsUnlimited storage account** used when configuring the integration service
5. Expand the **Queues** node
6. If the integration service has started, you should see two nodes: **product** and **orders**
7. Open the **product** queue by double clicking on it

You should see one or more messages generated by the integration service. If you don't see any messages, reconnect to the Linux VM using ssh and examine the output in the integration-service.log file.

Now that the integration service is in place, and the MRP system is capable of communicating with Azure via queues, it's time to look at how to integrate the e-commerce website.

## Integration Part 2: e-commerce site (WebJobs)

The Parts Unlimited e-commerce site is a public site which allows its customers to authenticate, create / review orders, view inventory levels, and view product lead times. As orders are placed via the website they need to be fulfilled by the existing MRP application. Similarly as stock levels change in the MRP app, the e-commerce site needs to be kept up to date.

### Review

Parts Unlimited have built two applications that will run as WebJobs alongside the existing Azure hosted e-commerce site. The existing e-commerce database will be used to read orders which have not yet been processed with in the MRP application and also store product inventory levels / lead times.
 
At the end of this section you will have two WebJobs which will be responsible for triggering the two-way communication between the integration service and the e-commerce app: 

1. Scheduled processing of new orders: new orders will flow from the website into a SQL database, where the web job then reads the unprocessed orders and writes a message to the `orders` queue within an Azure storage account

	> An alternative approach would be to place the order directly on to the queue as part of the checkout logic in the site. This would reduce the latency between the order being placed and then processed by the MRP.
2. Inventory updates: the web job will monitor the `product` queue and use the information contained in the incoming messages to update the SQL database with inventory levels and lead times 

![Communicating Inventory][7]

Both WebJobs are part of the extended website Visual Studio solution:

1. Open Visual Studio 2015
2. Open the solution file **PartsUnlimited.MRP.sln** (located in the directory set up as the clone path for the PartsUnlimited project)
3. Build the PartsUnlimited solution to ensure all required solution dependencies are resolved
4. The webjobs are implemented as two console apps:
  * PartsUnlimited.WebJobs.ProcessOrder: scheduled processing of new orders
  * PartsUnlimited.WebJobs.UpdateProductInventory: receives and processes inventory updates
5. Open `Functions.cs` from the PartsUnlimited.WebJobs.ProcessOrder project

	> The WebJobs SDK automatically obtains a connection to the `orders` queue and passes it to this static method. It will be configured to run on a scheduled basis as there is no trigger for this operation (note the `NoAutomaticTrigger` attribute). Once running, it finds unprocessed orders in the database and pushes them onto the `orders` queue.
6. Open `Functions.cs` from the PartsUnlimited.WebJobs.UpdateProductInventory project

	> The WebJobs SDK automatically obtains a connection to the `product` queue and calls this message whenever a message is available for processing. Once called, it updates the product record in the database.

### Deploy Process Orders WebJob

1. Open the `config.json` file under the **PartsUnlimited.WebJobs.ProcessOrder** project 
2. Set the database connection string and the Azure WebJob storage connection string
![Example configuration file][1]

	> Database connection string should be in the format:
	> `Server=tcp:{serverName}.database.windows.net,1433;Database={databaseName};User ID={userName}@{serverName};Password={password};Trusted_Connection=False;Encrypt=True;Connection Timeout=30;`
3. Within Solution Explorer, right click on the **PartsUnlimited.WebJobs.ProcessOrder** project and select **Publish**
4. In the Profile tab select **File System** as the publish target and enter a profile name (e.g. ProcessOrderWebJob)
5. Click **OK**
6. Click **Publish**
7. A link to the published webjob will be provided in the Output Window. **Open** this location

  	> `Web App was published successfully 	file:///C:/MSCorp.PartsUnlimited/artifacts/bin/PartsUnlimited.WebJobs.ProcessOrder/Release/Publish`
  	
 	![Example build folder][2]
8. **Zip** up the contents of the folder into a zip file called `ProcessOrderWebJob.zip`
9. Return to the [Azure Management Portal][portal]
10. **Navigate** to the new web app
11. Select the **Web Jobs** tab
12. Click **Add** and complete the dialog with the following values:
 * Name: Process Order
 * Browse: select the archive created above called `ProcessOrderWebJob.zip`
 * How to run: On a schedule
 * Recurrence: Recurring Job, recur every five minutes
13. Click **Complete**

The order processing WebJob has now been deployed. Orders placed by customers on the e-commerce site will be inserted directly into the MRP application for fulfillment and happy customers.

### Deploy Update Product Inventory WebJob

1. Edit the `config.json` file under the **PartsUnlimited.WebJobs.UpdateProductInventory** project
2. Set the database connection string and the Azure WebJob storage connection string
	![Example configuration file][1]

	> Database connection string should be in the format:
	> `Server=tcp:{serverName}.database.windows.net,1433;Database={databaseName};User ID={userName}@{serverName};Password={password};Trusted_Connection=False;Encrypt=True;Connection Timeout=30;`
3. Within Solution Explorer, right click on the **PartsUnlimited.WebJobs.UpdateProductInventory** project and select **Publish**
4. In the Profile tab select **File System** as the publish target and enter a profile name (e.g. UpdateProductInventoryWebJob)
5. Click **OK**
6. Click **Publish**
7. A link to the published webjob will be provided in the Output Window. **Open** this location

	> `Web App was published successfully	file:///C:/MSCorp.PartsUnlimited/artifacts/bin/PartsUnlimited.WebJobs.UpdateProductInventory/Release/Publish`
	
	![Example build folder][3]
8. **Zip** up the contents of the folder into a zip file called `UpdateProductInventoryWebJob.zip
9. Return to the [Azure Management Portal][portal]
10. **Navigate** to the new web app
11. Select the **Web Jobs** tab
12. Click **Add** and complete the dialog with the following values:
  * Name: UpdateProductInventory
  * Browse: select the archive created above called `UpdateProductInventoryWebJob.zip`
  * How to run: Run continuously
13. Click **Complete**

The inventory update webjob has now been deployed. Stock level and lead time changes made in the MRP app will now be visible in the e-commerce website.

## Explore integrated apps

Now that the e-commerce website and the MRP app are integrated, it's time to review and validate the deployment. During this section you will create orders through the website and and see them flow to the outsourced system as well as create manual inventory updates in the outsourced system and see updates flow through to the website.

### Order Processing

In this section you will use the e-commerce website to place a new order and the MRP REST API to view recorded orders. We can access the MRP endpoint because the application is artifically hosted in a VM in Azure. In the scenario, these endpoints would exist within the organization's network and would not be publically accessible.

1. Open **Postman**
2. **Create** a new `GET` request to `http://<your Linux VM DNS name>.cloudapp.net/orders`
3. **Send** the request
4. Take note of the list of orders. If you haven't run through this lab before, there should be two orders (with order IDs 0 & 1)
5. Open your **browser**
6. **Navigate** to the e-commerce website
7. Click on the **Batteries** category link near the top of the page
8. Click on the **Jumper leads** product
9. Click **Add to cart**
10. On the cart page, click **Checkout**
11. **Login** with the same user account used in the *Explore standalone applications* section.
12. On the payment page, complete the form and click **Submit Order**
13. Switch back to **Postman**
14. **Re-send** the postman request to refresh the list of orders. There is one more order now (the order just placed)

> If you don’t see an order, remember that the webjob only processes orders every 5 minutes. Wait for 5 minutes and then re-send the request.

### Inventory Update

In this section you will use the MRP REST API to update stock levels and lead times within the MRP application. Typically this would be completed as part of the normal Parts Unlimited business processes initiated during ordering and selling stock but because we want to simulate this artificially we will manually trigger these updates and then view the result in the e-commerce site.

1. Return to your **browser** and navigate to the website **home page** 
2. Click on the **Oil** category link near the top of the page
3. Click on the **Filter Set** product
4. Note that it is in stock and ships within 1-3 days (see fields under the price)
![Oil Filters stock level][9]
5. Return to **Postman**
6. **Create** a new `GET` request to `http://<your Linux VM DNS name>.cloudapp.net/catalog/OIL-0001`
   
	![Postman GET data][5]
 	> This content reflects the inventory state and the lead for delivery on new stock within the MRP system
7. Let's trigger an update to the inventory level to remove stock and signal that the lead time is 5 days
8. **Copy** the JSON response from the last message
7. **Create** a new `PUT` request to `http://<your Linux VM DNS name>.cloudapp.net/catalog/OIL-0001`
8. Set the `Content-Type` **header** to `application/json`
9. Set the **body** of the request to be the copied content from the original `GET`
10. Update the **inventory** to `0` and the **lead time** to `5`
11. **Send** the PUT request
12. **Re-send** the original `GET` to see the values change
13. Return to the browser and **refresh** the page displaying the 'Filter Set' product. The product is now out of stock and had a lead time of 5 - 6 days
![Oil Filters stock level][10]

	> If you still see it saying in stock, this could be for one of two reasons:
  	> 	1. The outsourced application is configured to send a message to the storage account with the current inventory and lead times every 30 seconds. Until this scheduled task runs and the webjob picks up the message, the website will not see the updated stock level.
  	> 	2. The website is configured to cache individual products for ten minutes to ensure adequate performance under load. The easiest way to invalidate the cache is to restart the site from the Azure management portal.


<!--Image references-->
[1]: ./hol-integration-services/config_shot.png
[2]: ./hol-integration-services/order_build.png
[3]: ./hol-integration-services/inventory_build.png
[5]: ./hol-integration-services/postman_get.png
[6]: ./hol-integration-services/isolated.png
[7]: ./hol-integration-services/communicating.png
[8]: ./hol-integration-services/java_integration.png
[9]: ./hol-integration-services/pre_inventory_update.png
[10]: ./hol-integration-services/post_inventory_update.png


<!--Reference style links - using these makes the source content way more readable than using inline links-->  
[pu_web]: https://github.com/microsoft/PartsUnlimited
[pu_web_started]: https://github.com/Microsoft/PartsUnlimited/blob/master/docs/GettingStarted.md
[pu_web_deploy]: https://github.com/Microsoft/PartsUnlimited/blob/master/docs/Deployment.md
[pu_mrp]: http://github.com/microsoft/PartsUnlimitedMRP
[pu_mrp_build]: https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/Build-MRP-App-Linux.md
[pu_mrp_deploy]: https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/HOL-Deploying-MRP-with-xPlat-CLI-Infrastructure-as-Code.md

[azure_sub]: https://account.windowsazure.com/Home/Index
[VS2015]: https://www.visualstudio.com/en-us/downloads/visual-studio-2015-downloads-vs.aspx
[WPI]: http://www.microsoft.com/web/downloads/platform.aspx
[powershell_commandlets]: https://azure.microsoft.com/en-us/documentation/articles/powershell-install-configure/#Install
[7Zip]: http://www.7-zip.org/download.html
[postman]: https://www.getpostman.com/
[putty]: http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html
[cli]: https://azure.microsoft.com/en-us/documentation/articles/xplat-cli/
[portal]: https://manage.windowsazure.com/
[AzureQueueVsServiceBus]: https://azure.microsoft.com/en-gb/documentation/articles/service-bus-azure-and-service-bus-queues-compared-contrasted/
