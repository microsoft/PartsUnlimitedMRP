#Build and Install MRP App 
This document explains how to build and manually install the MRP Application

To be able to run through this walkthrough there is a dependency on Java 8 SDK and JRE8. These can be found at the link below:
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

##Build the MRP Solution 
In order to build the MRP solution we will need to install some more dependencies. 
1.  To build the project, navigate to the OrderService folder with cmd/PowerShell console and initiate the build by calling the gradlew file with the parameter of the build.gradle file.

`PS> .\gradlew build`

Once run you can find the compiled jar file under the “build/libs” folder.

##Open the solution in Eclipse 

We recommend Eclipse IDE but you can use whatever IDE you want.

1. To be able to add the OrderService to Eclipse as a project first we need to add the plugin.
2.  Open the file build.gradle.
3.  Add the following line into the section of plugins.

`apply pulgin: 'eclipse'`

4. Run the following command in the PowerShell console

`PS:>.\gradlew eclipse
PS:>.\gradlew buildDependents`

5.  Open Eclipse and create a new workspace.
6.  Right click on the Project Explorer and select import a new project.
7.  Browse to the OrderService root folder and select it
8.  Check the OrderService project and click finish. 

##Create a Linux VM 
Create a Linux VM in Azure and signin onto it.

##Install Dependencies
1.  With you ssh console now open we need to add the following dependencies:
• Java 8 SDK
• Java 8 JRE
• MongoDB
2.  Enter the following command to install the dependencies above.
`sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb`

3.  To setup Java type the following commands to set your environment variables.
`export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin`

4.  To check your Java 8 installation, type “java -version”
NOTE: if you are still referencing an older version of Java that was installed as part of your machine you can update this using the following command:

`sudo update-alternatives --config java`
You will be presented with a list of Java installations. Type the number you wish to set as your java default.

##Setup MONGODB
1.  Open the mongo command line tools by typing the following command:
`/usr/bin/mongo`

2.  Select the ordering database to create it.
`> use ordering`

3. Add an object to the catalog collection
`> x = {"skuNumber" : "ACC-001", "description" : "Shelving", "unit" : "meters", "unitPrice" : 10.5 }
> db.catalog.insert(x)`

4.  Check the object was created 
`> db.catalog.find()`

5. Now there is data in the database you can check it with the following command 
`> show dbs`

6.  Copy the commands from Appendix A to insert sample data into the database 

##Setup WEBSERVICE


##APPENDIX A

db.catalog.insert(
[{"skuNumber" : "REF-687", "description" : "R404A", "unit" : "kilograms", "unitPrice" : 49.95 },
{"skuNumber" : "MAC-234", "description" : "Control, Pressure", "unit" : "", "unitPrice" : 129.95 },
{"skuNumber" : "MAC-613", "description" : "Solenoid Valve", "unit" : "", "unitPrice" : 89.95 },
{"skuNumber" : "REF-020", "description" : "Hybrid A/C Compressor", "unit" : "", "unitPrice" : 679.95 }])

db.dealers.insert({ "name" : "Terry Adams", "address" : "17760 Northeast 67th Court, Redmond, WA 98052", "email" : "terry@adams.com", "phone" : "425-885-6217" })

db.quotes.insert([{
"quoteId" : "0",
"validUntil" : "2015-05-01T00:00:00+0000",
"customerName" : "Walter Harp",
"dealerName" : "Terry Adams",
"terms" : "All work is to occur between 3pm and 5pm in the afternoon",
"unitDescription" : "Small compressor unit.",
"city" : "Seattle",
"unitCost" : "759.95",
"totalCost" : "759.95",
"discount" : "0.0",
"unit": "",
"height" : "420",
"width" : "275",
"depth" : "275",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98023",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Refrigerator",
"additionalItems" : []
},
{
"quoteId" : "1",
"validUntil" : "2015-01-01T00:00:00+0000",
"customerName" : "Jerry Morrison",
"dealerName" : "Terry Adams",
"terms" : "To be completed prior to final payment",
"unitDescription" : "Walk in Refrigerator",
"city" : "Seattle",
"unitCost" : "5699.95",
"totalCost" : "5599.95",
"discount" : "100.0",
"unit": "",
"height" : "240",
"width" : "3000",
"depth" : "2500",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98089",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Refrigerator",
"additionalItems" : []
},
{
"quoteId" : "2",
"validUntil" : "2015-02-01T00:00:00+0000",
"customerName" : "Harrison Hall",
"dealerName" : "Terry Adams",
"terms" : "Unit must fit in the 400x400x400 space built into the chiller",
"unitDescription" : "Freezer Unit.",
"city" : "Seattle",
"unitCost" : "489.95",
"totalCost" : "489.95",
"discount" : "0.0",
"unit": "",
"height" : "400",
"width" : "400",
"depth" : "400",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98027",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Freezer",
"additionalItems" : []
}
])

db.orders.insert([{
"orderId" : "0",
"quoteId" : "0",
"orderDate" : "2015-03-02T20:43:37+0000",
"status" : "Created",
"events" : []
},
{"orderId" : "1",
"quoteId" : "2",
"orderDate" : "2015-03-02T20:43:37+0000",
"status" : "DeliveryConfirmed",
"events" : []
}])

db.shipments.insert([{
"orderId" : "0",
"contactName" : "Walter Harp",
"primaryContactPhone" : {
  "phoneNumber" : "435-783-2378",
  "kind" : "Mobile"
},
"deliveryAddress" : {
  "street" : "34 Sheridan Street",
  "city" : "Seattle",
  "state" : "WA",
  "postalCode" : "98023",
  "specialInstructions" : ""
},
"events" : []
},
{
"orderId" : "2",
"contactName" : "Harrison Hall",
"primaryContactPhone" : {
  "phoneNumber" : "435-712-7234",
  "kind" : "Mobile"
},
"deliveryAddress" : {
  "street" : "84 Queen Street",
  "city" : "Seattle",
  "state" : "WA",
  "postalCode" : "98027",
  "specialInstructions" : "To be installed on meat freezer 3."
},
"events" : []
}])
