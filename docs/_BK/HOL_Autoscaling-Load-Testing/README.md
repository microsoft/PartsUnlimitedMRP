#HOL - Auto-Scaling and Load Testing

Your Linux Azure virtual machine has suffered significant performance degradation during Black Friday. The business unit responsible for the website’s functionality has complained to IT staff that users would intermittently lose access to the website, and that load times were significant for those who could access it.

In this lab, you will learn how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you will create a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services. 

**Pre-requisites**

- The PartsUnlimitedMRP Linux Azure virtual machine set up and deployed with endpoint 9080 open (see [link](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/Build-MRP-App-Linux.md))

- Visual Studio Ultimate license

**Tasks**

1. Setting up and running a load test in Visual Studio Team Services
2. Creating virtual machines with Azure CLI
3. Configuring with availability groups in Azure Management Portal
4. Running a load test to verify auto-scaling

###Task 1: Setting up and running a load test in Visual Studio Team Services

Performing a load test can be done in Visual Studio, in a browser in Visual Studio Team Services, or in the new Azure Portal. For simplicity, we will run a load test in a browser in Visual Studio Team Services. 

**1.** Open a web browser and navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Team Services account, such as:

    https://{VSTS instance}.visualstudio.com

On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<media/navigate_to_load_test_tab.png>)

**2.** In the load test tab, create a simple load test in the browser. Click on the **New** button and select **URL-based test** to create a new URL-based test.

![](<media/select_url_test.png>)

**3.** Name the load test *PartsUnlimitedMRP Homepage Load Test*. Specify the home page URL, which should be the URL to MRP with your virtual machine name and port (such as *http://{mycloudhostname}.cloudapp.net:9080/mrp*).

![](<media/point_to_mrp_url.png>)

**4.** Select the **Settings** tab and change the **Run duration** to 1 minute. You can optionally change the max virtual users, browser mix, and load location as well. Then click the **Save** button.

![](<media/change_run_duration.png>)

**Step 4.** Click on the **Run test** button to begin the test. The load test will start running and show metrics in real time. 

![](<media/run_test.png>)

**Step 5.** When the test has finished loading, it will show metrics, errors, and application performance. We should be able to solve this issue by creating an availability set for the virtual machines and configuring auto-scaling.
![](<media/view_test_results.png>)

###Task 2: Creating virtual machines with Azure CLI
Before configuring auto-scaling, we need to create a virtual machine and add it to an availability set. We can use Azure Command Line tools on our Linux machine to create a virtual machine and availability set. 

**1.** Ensure that the PartsUnlimitedMRP machine is running. SSH into the machine with your credentials. 

    ssh <login>@<dnsname>.cloudapp.net

![](<media/ssh_virtual_machine.png>)

**2.** Before installing azure-cli tools, install nodejs-legacy and and npm: 

    sudo apt-get install nodejs-legacy
	sudo apt-get install npm

![](<media/apt_get_nodejs_legacy.png>)

![](<media/apt_get_npm.png>)


**3.** Now that nodejs-legacy and npm are installed, type the following to download azure-cli: 

    sudo npm install azure-cli

![](<media/npm_install_azure_cli.png>)

Make sure that Azure Service Management is configured (default upon installation).

**4.** Log in to azure-cli with your organizational credentials:

    azure login

![](<media/login_azure_cli.png>)

**5.** To use availability sets, more than one virtual machine must be in the set. We also will create a virtual machine in the same cloud service as the PartsUnlimitedMRP machine. Create a new virtual machine with the following: 

`{dnsname}.cloudapp.net` - Replace {dnsname} with the cloud service DNS name of the PartsUnlimitedMRP machine. 

`"b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-16_04-LTS-amd64-server-20160627-en-us-30GB"` - The Linux 16.04 LTS latest VM image. Paste this value into the SSH client when running the command. 

`-n "{vm name}"` - Replace {vm name} with the name of the new virtual machine.

`-g "{username}"` - Replace {username} with the username for this new machine.

`-p "{password}"` - Replace {password} with the password for this new machine. 

`-A "{availset}"` - Replace {availset} with the name of an availability set. If the set doesn't exist already, it will create a new one with the specified name. 

`-c "{dnsname}"` - Replace {dnsname} with your cloud service name. It should match `{dnsname}.cloudapp.net`. 

`-z "Medium"` - Set the size of the VM to be created as Extra Small (A2, 3.5 GB memory). This should match the size of the PartsUnlimitedMRP machine. 

	azure vm create "{dnsname}.cloudapp.net" "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-16_04-LTS-amd64-server-20160627-en-us-30GB" -n "{vm name}" -g "{username}" -p "{password}" -A "{availset} -c "{dnsname}" -z "Medium"

For example: 

	azure vm create "partsunlimiteddev.cloudapp.net" "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_3-LTS-amd64-server-20150805-en-us-30GB" -n "partsunlimiteddev2" -g "azureuser" -p "P2ssw0rd!" -A "mrp-availset" -c "partsunlimiteddev" -z "Medium"

It may take a few minutes for the virtual machine to be created. 

![](<media/create_vm_azure_cli>)

**6.** Once the virtual machine has been created, open a web browser and log in to the Azure Management Portal, [https://manage.windowsazure.com](https://manage.windowsazure.com). 

**7.** Click on the **Virtual Machines** tile on the left-hand column. Note that the new virtual machine that you created now is listed with the same DNS name. Click on the name of the new machine that you just created for the availability set. 

![](<media/select_vm_portal.png>)

**Step 8.** In the new machine **Configure** tab, note that there is an important message that there needs to be at least two running virtual machine instances for the new availability set. We will add the PartsUnlimitedMRP machine into that availability set. Then click on the name of the PartsUnlimitedMRP machine on the left. 

![](<media/view_second_vm_portal.png>)

**Step 9.** In the PartsUnlimitedMRP **Configure** tab for the machine, click on the dropdown next to **Availability Set** and select the availability set that you created in azure-cli. Then click on the **Save** tile at the bottom of the page. We are able to add this machine to the availability set because it is in the same cloud service as the other machine. 

![](<media/view_first_vm_portal.png>)

![](<media/view_availability_set.png>)

**Step 10.** Click on the large back button on the upper-left area of the page and click on the **Cloud Services** tile on the left column. Click on the name of the cloud service for the PartsUnlimitedMRP machine. 

![](<media/view_cloud_services_portal.png>)

**Step 11.** In the PartsUnlimitedMRP cloud service, click on the **Scale** tab. Note that both of the virtual machines are listed under **Instances**. Since both machines are in the availability set, we can set auto-scaling so that Azure will automatically scale up and down the virtual machines in the availability group based on CPU. Select **CPU** next to **Scale by metric**. The instance range will cover both instances and the target CPU default is 60 - 80%. Click the Save button at the bottom of the page to save the auto-scaling configuration settings for the availability set.

![](<media/scale_cloud_service_portal.png>)

###Task 3: Running a load test to verify auto-scaling

We now have two virtual machines in an availability set that scales by CPU so that whenever the CPU percentage for PartsUnlimitedMRP is over the threshold of 80%, Azure will automatically add an instance to the virtual machine. We can now run a load test again to compare the results. 

**1.** Navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Team Services account, such as:

    https://{VSTS instance}.visualstudio.com

On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<media/navigate_to_load_test_tab.png>)

**2.** Select the Load Test previously created in Task 1, then click on the **Run test** button to begin the test. The load test will start running and show metrics in real time. 

![](<media/second_load_test_summary.png>)

The average response time has improved by autoscaling multiple virtual machines in Azure based on CPU load. 

In this lab, you learned how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you created a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services.

Next steps
----------

-   [HOL Parts Unlimited MRP Continuous Integration ](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Integration)

-   [HOL Parts Unlimited MRP Automated Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Automated-Testing)

-   [HOL Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)