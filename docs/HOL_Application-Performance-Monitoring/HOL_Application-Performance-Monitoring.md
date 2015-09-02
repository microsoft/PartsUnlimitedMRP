#Application Performance Monitoring

The DevOps team has noticed that the Dealers page is slow to load and shows performance spikes with database calls in the Application Insights telemetry. By viewing the details of performance monitoring through Application Insights, we will be able to drill down to the code that has affected the slow performance of the web application and fix the code.

In this lab, you will learn how to set up Application Insights telemetry, and drill down into performance monitoring data through Application Insights in the new Azure Portal.

**Prerequisites**

- Code Editor (VSCode, Eclipse, etc.)

- PartsUnlimitedMRP Linux Azure virtual machine set up and deployed with endpoint 9080 open (see [link](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/Build-MRP-App-Linux.md))

- Application Insights created in the same Azure Resource Group as the PartsUnlimitedMRP virtual machine (see [link](https://azure.microsoft.com/en-us/documentation/articles/app-insights-create-new-resource/))

- Continuous Integration build with Gradle to the PartsUnlimitedMRP virtual machine (see [link](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment-with-Visual-Studio-Online-Build))

**Tasks Overview**

1. Set up Application Insights for PartsUnlimitedMRP

2. Using Application Performance Monitoring to resolve performance issues

###Task 1: Set up Application Insights for PartsUnlimitedMRP
**Step 1.** In an Internet browser, navigate to <http://portal.azure.com> and
sign in with your credentials.

![](<media/prereq-step1.png>)

**Step 2.** Click on the “Browse All” tile on the left column, select
“Application Insights,” and click on the name of the telemetry that you have created such as "PartsUnlimitedMRP-Insights."

![](<media/prereq-step2.png>)

**Step 3.** In the Application Insights telemetry for PartsUnlimitedMRP, select the Settings tile, followed by the Properties tile to find the Instrumentation key. 

![](<media/prereq-step3.png>)

**Step 4.** Copy the Instrumentation Key in the Properties panel. You will need this when inserting the key into the ApplicationInsights.xml file in PartsUnlimitedMRP's resources folder. 

![](<media/prereq-step4.png>)

**Step 5.** Navigate to the working folders of the PartsUnlimitedMRP repo in a code editor (such as VSCode). 

![](<media/prereq-step5.png>)

**Step 6.** In `PartsUnlimitedMRP/src/Backend/OrderService/build.gradle`, confirm that the build file is importing `com.microsoft.appinsights.*` and is also compiling `com.microsoft.azure:applicationinsights-core:0.9.0`.

![](<media/prereq-step6.png>)

**Step 7.** In `PartsUnlimitedMRP/src/OrderService/src/main/resources/ApplicationInsights.xml`, paste in the instrumentation key that you copied previously from the Azure Portal in between the `<InstrumentationKey>` tags. 

![](<media/prereq-step7.png>)

**Step 8.** Additionally, paste in the following telemetry modules and telemetry initializers in between the `<TelemetryModules>` and `<TelemetryIntializers>` tags and save the changes. 

Telemetry Modules:  

	<Add type="com.microsoft.applicationinsights.web.extensibility.modules.WebRequestTrackingTelemetryModule"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.modules.WebSessionTrackingTelemetryModule"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.modules.WebUserTrackingTelemetryModule"/>		

Telemetry Initializers:

	<Add type="com.microsoft.applicationinsights.web.extensibility.initializers.WebOperationIdTelemetryInitializer"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.initializers.WebOperationNameTelemetryInitializer"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.initializers.WebSessionTelemetryInitializer"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.initializers.WebUserTelemetryInitializer"/>
    <Add type="com.microsoft.applicationinsights.web.extensibility.initializers.WebUserAgentTelemetryInitializer"/>

![](<media/prereq-step8.png>)

**Step 9.** Return to the Azure Portal and under the Application Insights telemetry for PartsUnlimitedMRP, click on the tile in the overview timeline for application health, "Learn how to collect browser page load data." Once you click on it, a new panel should open that shows the end-user usage analytics code. Copy lines 8 through 17 (the script itself). 

![](<media/prereq-step9.png>)

**Step 10.** Back in the code editor, we will want to insert the script code previously copied before the end of the `<HEAD>` tag for each of the HTML pages in PartsUnlimitedMRP, starting with the index page. In `PartsUnlimitedMRP/src/Clients/Web/index.html`, paste the script code before the other scripts inside of the `<HEAD>` tag. 

![](<media/prereq-step10.png>)

**Step 10.** Repeat step 10 for the following HTML files:

- `PartsUnlimitedMRP/src/Clients/Web/pages/catalog/catalog.html`
- `PartsUnlimitedMRP/src/Clients/Web/pages/dealers/dealers.html`
- `PartsUnlimitedMRP/src/Clients/Web/pages/deliveries/deliveries.html`
- `PartsUnlimitedMRP/src/Clients/Web/pages/orders/orders.html`
- `PartsUnlimitedMRP/src/Clients/Web/pages/quotes/quotes.html`

**Step 11.** Commit and push the changes to kick off the Continuous Integration build with Gradle. 

![](<media/prereq-step11.png>)

![](<media/prereq-step11b.png>)

**Step 12.** Return to the Azure Portal into the PartsUnlimitedMRP Application Insights telemetry to find data available for browser page loading and dependency durations. It may take a few moments for Application Insights to reload.

![](<media/prereq-step12.png>)

###Task 2: Using Application Performance Monitoring to resolve performance issues

**Step 1.** In an Internet browser, navigate to the PartsUnlimitedMRP website that you previously deployed and go to the Dealers page. You'll notice immediately that the page takes a while for the dealers to load on the left-hand side. 

![](<media/step1.png>)

**Step 2.** Click on the “Browse All” tile on the left column, select
“Application Insights,” and click on the name of your Application Insights
telemetry for your web app.

![](<media/prereq-step2.png>)

**Step 3.** After selecting the Application Insights telemetry for your web app,
scroll down and select the “Performance” tile to view performance monitoring
information.

![](<media/step3.png>)

**Step 4.** In the performance tile of the Application Insights telemetry, note
the timeline. The timeline data may not show up immediately, so you will want to wait for a few minutes for the telemetry to collect performance data. 

![](<media/step4.png>)

**Step 5.** Once data shows in the timeline, view the operations listed under the **Average
of server response time by operation name** section under the timeline. Click on the top operation in the list referring to the Dealers page to view details of that operation.

**Step 6.** Drill down into the method that is affecting the slow performance. We now know where the slow performance is being caused in our code and that this is causing inefficient database calls. 

**Step 7.** Navigate to the working folders of the PartsUnlimitedMRP repo in a code editor (such as VSCode). 

![](<media/prereq-step5.png>)

**Step 8.** Find the `getDealers()` method in `PartsUnlimitedMRP/src/Backend/OrderService/src/main/java/smpl/ordering/controllers/DealerController.java` that is causing slow performance.

![](<media/step8.png>)

**Step 9.** In the `getDealers()` method, notice that there is a database call 1000 times with the variable, `numMongoDBCalls`. Change the value of this variable to be 1 so that there is only one call to the database to populate the dealers list. 

![](<media/step9.png>)

**Step 10.** Save the changes and commit the changes on the master branch. Push the changes to the remote repo in VSO to kick off a Continuous Integration build. 

![](<media/step10.png>)

![](<media/prereq-step11b.png>)

**Step 11.** Now that our changes have deployed to the website, open up a new incognito browser window (to prevent caching) and return to the Dealers page. The dealers will show up faster than they did previously now having one call to the database. 

![](<media/step1.png>)

**Step 12.** Return to the Application Insights performance monitoring view in the Azure Preview Portal and refresh the page. The **Average of server response time by operation name** overview should not be showing the `getDealers()` method.


In this lab, you learned how to set up Application Insights telemetry, and drill down into performance
monitoring data through Application Insights in the new Azure Portal.

Try these labs out for next steps:

- Automated Testing hands-on lab
- Load Testing and Autoscaling hands-on lab
- Automated Recovery hands-on lab
- HDD: User Telemetry hands-on lab

**Further Resources**

[Get started with Application Insights in a Java web project](https://azure.microsoft.com/en-us/documentation/articles/app-insights-java-get-started/)

[Unix performance metrics in Application Insights](https://azure.microsoft.com/en-us/documentation/articles/app-insights-java-collectd/)

[Application Insights API for custom events and metrics](https://azure.microsoft.com/en-us/documentation/articles/app-insights-web-track-usage-custom-events-metrics/)