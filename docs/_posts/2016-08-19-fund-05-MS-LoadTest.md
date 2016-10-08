---
layout: page
title:  Auto-Scale & Load Tests
category: FundVSTS
order: 7
---

### Auto-Scaling and Load Testing

Your Linux Azure virtual machine has suffered significant performance degradation during Black Friday. The business unit responsible for the website’s functionality has complained to IT staff that users would intermittently lose access to the website, and that load times were significant for those who could access it.

In this lab, you will learn how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you will create a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services. 

### Video ###

<iframe src="https://channel9.msdn.com/Series/Parts-Unlimited-MRP-Labs/Parts-Unlimited-MRP-Application-Performance-Monitoring/player" width="960" height="540" allowFullScreen frameBorder="0"></iframe>

**Pre-requisites**

- The PartsUnlimitedMRP Linux Azure virtual machine set up and deployed with endpoint 9080 open (see [link](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/Build-MRP-App-Linux.md))

- Visual Studio Ultimate license

**Tasks**

1. Setting up and running a load test in Visual Studio Team Services
2. Auto-Scaling - WORKING ON IT
4. Running a load test to verify auto-scaling

### Task 1: Setting up and running a load test in Visual Studio Team Services ###

Performing a load test can be done in Visual Studio, in a browser in Visual Studio Team Services, or in the new Azure Portal. For simplicity, we will run a load test in a browser in Visual Studio Team Services. 

**1.** Open a web browser and navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Team Services account, such as:

    https://{VSTS instance}.visualstudio.com

On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<../assets/ASloadtests/navigate_to_load_test_tab.png>)

**2.** In the load test tab, create a simple load test in the browser. Click on the **New** button and select **URL-based test** to create a new URL-based test.

![](<../assets/ASloadtests/select_url_test.png>)

**3.** Name the load test *PartsUnlimitedMRP Homepage Load Test*. Specify the home page URL, which should be the URL to MRP with your virtual machine name and port (such as *http://{mycloudhostname}.cloudapp.net:9080/mrp*).

![](<../assets/ASloadtests/point_to_mrp_url.png>)

**4.** Select the **Settings** tab and change the **Run duration** to 1 minute. You can optionally change the max virtual users, browser mix, and load location as well. Then click the **Save** button.

![](<../assets/ASloadtests/change_run_duration.png>)

**Step 4.** Click on the **Run test** button to begin the test. The load test will start running and show metrics in real time. 

![](<../assets/ASloadtests/run_test.png>)

**Step 5.** When the test has finished loading, it will show metrics, errors, and application performance. We should be able to solve this issue by creating an availability set for the virtual machines and configuring auto-scaling.
![](<../assets/ASloadtests/view_test_results.png>)

### Task 2: Auto-Scaling with Microsoft Azure ###

**Working on it**

### Task 3: Running a load test to verify auto-scaling ###

We now have two virtual machines in an availability set that scales by CPU so that whenever the CPU percentage for PartsUnlimitedMRP is over the threshold of 80%, Azure will automatically add an instance to the virtual machine. We can now run a load test again to compare the results. 

**1.** Navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Team Services account, such as:

    https://{VSTS instance}.visualstudio.com

On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<../assets/ASloadtests/navigate_to_load_test_tab.png>)

**2.** Select the Load Test previously created in Task 1, then click on the **Run test** button to begin the test. The load test will start running and show metrics in real time. 

![](<../assets/ASloadtests/second_load_test_summary.png>)

The average response time has improved by autoscaling multiple virtual machines in Azure based on CPU load. 

In this lab, you learned how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you created a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services.

Next steps
----------

-   [HOL Parts Unlimited MRP Continuous Integration ](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Integration)

-   [HOL Parts Unlimited MRP Automated Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Automated-Testing)

-   [HOL Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)