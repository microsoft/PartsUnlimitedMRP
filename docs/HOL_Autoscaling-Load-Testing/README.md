#Auto-Scaling and Load Testing

Your Linux Azure virtual machine has suffered significant performance degradation during Black Friday. The business unit responsible for the website’s functionality has complained to IT staff that users would intermittently lose access to the website, and that load times were significant for those who could access it.

In this lab, you will learn how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you will create a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services. 

**Prerequisites**

- PartsUnlimitedMRP Linux Azure virtual machine set up and deployed with endpoint 9080 open (see [link](https://github.com/Microsoft/PartsUnlimitedMRP/blob/master/docs/Build-MRP-App-Linux.md))

- Visual Studio Ultimate license

**Tasks**

1. Setting up and running a load test in Visual Studio Online
2. Creating virtual machines with Azure CLI
3. Configuring 
4.  with availability groups in Azure Management Portal
4. Running a load test to verify auto-scaling

###Task 1: Setting up and running a load test in Visual Studio Online

Performing a load test can be done either in Visual Studio or in a browser in Visual Studio Online. For simplicity, we will run a load test in a browser in Visual Studio Online. 

**Step 1.** Open a web browser and navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Online account. On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<media/step1.png>)

**Step 2.** In the load test tab, you can create a simple load test in the browser. Specify the home page URL, which should be the URL to MRP with your virtual machine name and port (such as [https://mycloudhostname.cloudapp.net:9080/mrp](https://mycloudhostname.cloudapp.net:9080/mrp)). Give the load test a friendly name and specify the load location. 

![](<media/step2.png>)

**Step 3.** Select 100 users for load, 1 minute for the run duration, 1 second of think-time, and 100% IE for browser distribution. Click the **Test now** button to run your load test. 

![](<media/step3.png>)

**Step 4.** The load test will start running and show metrics in real time. 

![](<media/step4.png>)

**Step 5.** When the test has finished loading, it will show metrics, errors, and application performance. We should be able to solve this issue by creating an availability set for the virtual machines and configuring auto-scaling.
![](<media/step5.png>)

###Task 2: Creating virtual machines with Azure CLI
Before configuring auto-scaling, we need to create a virtual machine and add it to an availability set. We can use Azure Command Line tools on our Linux machine to create a virtual machine and availability set. 

**Step 1.** Ensure that the PartsUnlimitedMRP machine is running. SSH into the machine with your credentials. 

    ssh <login>@<dnsname>.cloudapp.net

![](<media/task2-step1.png>)

**Step 2.** Before installing azure-cli tools, install nodejs-legacy and and npm: 

    sudo apt-get install nodejs-legacy
	sudo apt-get install npm

![](<media/task2-step2a.png>)

![](<media/task2-step2b.png>)


**Step 3.** Now that nodejs-legacy and npm are installed, type the following to download azure-cli (globally): 

    sudo npm install -g azure-cli

![](<media/task2-step3.png>)

![](<media/task2-step3b.png>)

**Step 4.** Log in to azure-cli with your organizational credentials:

    azure login

![](<media/task2-step4.png>)

**Step 5.** To use availability sets, more than one virtual machine must be in the set. We also will create a virtual machine in the same cloud service as the PartsUnlimitedMRP machine. Create a new virtual machine with the following: 

`{dnsname}.cloudapp.net` - Replace {dnsname} with the cloud service DNS name of the PartsUnlimitedMRP machine. 

`"b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_3-LTS-amd64-server-20150805-en-us-30GB"` - The Linux 14.04 LTS latest VM image. Paste this value into the SSH client when running the command. 

`-n "{vm name}"` - Replace {vm name} with the name of the new virtual machine.

`-g "{username}"` - Replace {username} with the username for this new machine.

`-p "{password}"` - Replace {password} with the password for this new machine. 

`-A "{availset}"` - Replace {availset} with the name of an availability set. If the set doesn't exist already, it will create a new one with the specified name. 

`-c "{dnsname}"` - Replace {dnsname} with your cloud service name. It should match `{dnsname}.cloudapp.net`. 

`-z "ExtraSmall"` - Set the size of the VM to be created as Extra Small (A0, 768 MB memory). This should match the size of the PartsUnlimitedMRP machine. 

	azure vm create "{dnsname}.cloudapp.net" "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_3-LTS-amd64-server-20150805-en-us-30GB" -n "{vm name}" -g "{username}" -p "{password}" -A "{availset} -c "{dnsname}" -z "ExtraSmall"

For example: 

	azure vm create "pu-sachi.cloudapp.net" "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_3-LTS-amd64-server-20150805-en-us-30GB" -n "pu-sachi2" -g "sachi" -p "P2ssw0rd!" -A "mrp-availset" -c "pu-sachi" -z "ExtraSmall"

It may take a few minutes for the virtual machine to be created. 

![](<media/task2-step5.png>)

**Step 6.** Once the virtual machine has been created, open a web browser and log in to the Azure Management Portal, [https://manage.windowsazure.com](https://manage.windowsazure.com). 

![](<media/task2-step6.png>)

**Step 7.** Click on the **Virtual Machines** tile on the left-hand column. Note that the new virtual machine that you created now is listed with the same DNS name. Click on the name of the new machine that you just created for the availability set. 

![](<media/task2-step7.png>)

**Step 8.** In the new machine **Configure** tab, note that there is an important message that there needs to be at least two running virtual machine instances for the new availability set. We will add the PartsUnlimitedMRP machine into that availability set. Then click on the name of the PartsUnlimitedMRP machine on the left. 

![](<media/task2-step8.png>)

**Step 9.** In the PartsUnlimitedMRP **Configure** tab for the machine, click on the dropdown next to **Availability Set** and select the availability set that you created in azure-cli. Then click on the Save tile at the bottom of the page. We are able to add this machine to the availability set because it is in the same cloud service as the other machine. 

![](<media/task2-step9.png>)

![](<media/task2-step9b.png>)

**Step 10.** Click on the large back button on the upper-left area of the page and click on the **Cloud Services** tile on the left column. Click on the name of the cloud service for the PartsUnlimitedMRP machine. 

![](<media/task2-step10.png>)

**Step 11.** In the PartsUnlimitedMRP cloud service, click on the **Scale** tab. Note that both of the virtual machines are listed under **Instances**. Since both machines are in the availability set, we can set auto-scaling so that Azure will automatically scale up and down the virtual machines in the availability group based on CPU. 

![](<media/task2-step11.png>)

**Step 12.** In the availability set, select **CPU** next to **Scale by metric**. The instance range will cover both instances and the target CPU default is 60 - 80%. Additionally, keep the scale up and down by settings. Click the Save button at the bottom of the page to save the auto-scaling configuration settings for the availability set. 

![](<media/task2-step12.png>)

###Task 3: Running a load test to verify auto-scaling

We now have two virtual machines in an availability set that scales by CPU so that whenever the CPU percentage for PartsUnlimitedMRP is over the threshold of 80%, Azure will automatically add an instance to the virtual machine. We can now run a load test again to compare the results. 

**Step 1.** Open a web browser and navigate to the Team Project Collection ("DefaultCollection") of your Visual Studio Online account. On the upper-left set of tabs, click on "Load test" to open up load test options in the browser. 

![](<media/step1.png>)

**Step 2.** In the load test tab, specify the home page URL, which should be the URL to MRP with your virtual machine name and port (such as [https://mycloudhostname.cloudapp.net:9080/mrp](https://mycloudhostname.cloudapp.net:9080/mrp)). Give the load test a friendly name and specify the load location. 

![](<media/task3-step2.png>)

**Step 3.** Select 100 users for load, 1 minute for the run duration, 1 second of think-time, and 100% IE for browser distribution. Click the **Test now** button to run your load test. 

![](<media/task3-step3.png>)

**Step 4.** The load test will start running and show metrics in real time. 

![](<media/task3-step4.png>)

**Step 5.** When the test has finished loading, it will show metrics, errors, and application performance. We can compare these results with the previous load test's results and note that there has been an improvement in average response time and failed requests. 

![](<media/task3-step5.png>)

In this lab, you learned how to perform load testing against an endpoint for the PartsUnlimitedMRP Linux Azure virtual machine. Additionally, you created a virtual machine and availability set using Azure Command Line tools, as well as add both to a shared availability set to configure auto-scaling the set in cloud services. 

Try these labs out for next steps:

- Automated Testing hands-on lab
- Provisioning and Deploying Environments using Chef hands-on lab
- Automated Recovery hands-on lab
- HDD: User Telemetry hands-on lab

**Further Resources**

[Install the Azure CLI](https://azure.microsoft.com/en-us/documentation/articles/xplat-cli-install/)

[Install and Configure the Azure CLI](https://azure.microsoft.com/en-us/documentation/articles/xplat-cli/)

[Using the Azure CLI for Mac, Linux, and Windows with Azure Service Management ](https://azure.microsoft.com/en-us/documentation/articles/virtual-machines-command-line-tools/)