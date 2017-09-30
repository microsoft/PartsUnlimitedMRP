---
layout: page
title: Set up Parts Unlimited MRP with Jenkins
category: FundOth
order: 1
---

### Set up Parts Unlimited MRP with Jenkins ###

In this lab, we will set up the Jenkins Master that will be used for the PartsUnlimitedMRP project. [Jenkins](https://jenkins.io/) is an open source automation server that provides capabilities for building, deploying and automating any project. This lab will configure the pre-requisites on the Jenkins Master, if you are familiar with Jenkins and already have a Jenkins Master, you may want to skip to the next step for [Parts Unlimited MRP Continous Integration](2017-08-22-fund-11-Oth-CI.md).


### Pre-Requisites: ###

- An Active Azure Subscription. You can [open a trial subscription](https://azure.microsoft.com/en-us/free).
- A tool to connect to a Linux vm using SSH. In this tutorial we will be using [Putty](http://www.chiark.greenend.org.uk/~sgtatham/putty/).


### Tasks Overview:
In this tasks will install a virtual machine running Ubuntu with Jenkins in Azure then perform the basic configuration of Jenkins.

### Task 1: Create you Jenkins Master in Azure 
We will use the pre-configured VM image that is available on the Azure to deploy our Jenkins master.

**1.** To deploy the Jenkins Master

* You can [deploy the new resource by simply clicking here](https://portal.azure.com/#create/azure-oss.jenkinsjenkins) or in Azure Portal click `New` and search for Jenkins image from Microsoft.

![Deploy Jenkins](<../assets/jenkins2/pre/1.jpg>)
* Click `Create` to start the new resource configuration.

* Basic Configuration

> * Give the resource a name, such as `Jenkins`
> * Create a User Name, such as `jenkinsadmin`.
> * Type the password for the user account.
> * We recommend keeping the release type as `LTS`.
> * Select a subscription.
> * In this lab we will name the resource group as `MS.PU.Jenkins`.
> * Select the best location to deploy your resource.
> * Click `Ok` to move the the next step.

![Deploy Jenkins](<../assets/jenkins2/pre/2.jpg>)

* Settings
> * We recommend leaving all options as default.
> * Create a unique `domain name label`.
> * Click in `Ok` to move the the next step.

![Deploy Jenkins](<../assets/jenkins2/pre/3.jpg>)

* Summary
> * After the validation you can quickly review the information and click `Ok`.

![Deploy Jenkins](<../assets/jenkins2/pre/4.jpg>)

* Buy
> * Click on the `Purchase` Button. The deployment of this new resource will start.

![Deploy Jenkins](<../assets/jenkins2/pre/5.jpg>)

**2.** Wait until the deployment complete. It may take some minutes.

![Deploy Jenkins](<../assets/jenkins2/pre/6.jpg>)

**3.** Obtain the public IP of the Jenkins master VM. 
* On Azure portal, click on `Resource Group` and look for the new resource Group created in the previous step.
* Select the Jenkins Virtual Machine.
* On the overview tab you can find the `DNS Name` and `Public IP address` for the VM.

![Obtain the public IP of the Jenkins Master](<../assets/jenkins2/pre/7.jpg>)


**4.** SSH to the Jenkins Master VM
* Open Putty to create a new connection session.
* Set the Host Name as `jankinsadmin@DNS_Name`, for example: `jenkinsadmin@mspujenkins.australiasoutheast.cloudapp.azure.com`.

![SSH to the Jenkins Master VM](<../assets/jenkins2/pre/8.jpg>)

* Expand the Menu on the left and click on `SSH\Tunnels`.
* Add new forwarded port: Source port to `8080` and Destination to `localhost:8080`. Click `Add`.

![Setup SSH Tunnel](<../assets/jenkins2/pre/9.1.jpg>)

* Click on `Open`.
* Select `Yes` on the Security Alert.

![Setup SSH Tunnel](<../assets/jenkins2/pre/9.jpg>)

* Then type the password that you have specified for `jenkinsadmin` when deploying the machine on Azure.
* Keep this session open until we finish this HOL. Do not close Putty application.

**Note:**
We will be using a Tunnel so we can access Jenkins from a public IP, by default Jenkins is configured to block public access for security reasons and in this tutorial we will not cover how to configure a secure Jenkins environment for Public access. Please check [Jenkins instructions](https://jenkins.io/blog/2017/04/20/secure-jenkins-on-azure/) for more details on how to configure Jenkins certificate.

### Task 2: Configure your Jenkins Master
In this task, we will perform the basic configuration of the Jenkins master server and install the necessary plugins that will be used for Continuous Integration.

**1.** Obtain the initial admin password

* From the SSH session that you have opened at the end of Task 1, type the following command to obtain the initial admin password.

``` bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

* Copy the value returned by the command.
Keep the SSH session open, we will return at the end of this task and use it in CI and CD tasks.

**Note:** To copy from Putty terminal, simple select the text you want to copy. To paste, use the mouse right click. 

**2.** Unlock the jenkins master

* With your browser, navigate to the default page of the Jenkins master using the created tunnel. 

```
http://localhost:8080
```

**Note:** If you don't have a Tunnel defined on Putty as specified on the previous step, or have closed Putty, and try to access this address you will get an error page. Also if you try to access Jenkins using the public DNS you will get a error page form Jenkins.

* Paste the initial admin password obtained earlier to unlock your instance of Jenkins and click **Continue**.

![Initial jenkins admin password](<../assets/jenkins2/pre/10.jpg>)


* Click **Install suggested plugins**

![Initial jenkins plugins](<../assets/jenkins2/pre/11.jpg>)

**3.** Create the first user 

* Create a user from the "Create First Admin User": 
> * Username: jenkins
> * Password: Passw0rd
> * Full name: Jenkins admin
> * E-mail address: jenkins@microsoft.com

* Click **Save and Finish**. 

![Initial jenkins plugins](<../assets/jenkins2/pre/12.jpg>)

**4.** Start using Jenkins

* Click the **Start using Jenkins** button to login as admin. 

* You now have a virtual machine in Azure running Jenkins as a Master.

**5.** Logon on the jenkins master with the credentials that you have just created in the previous step. 
* Username: jenkins 
* Password: Passw0rd

**6.** Navigate to the "Configure System" page:

```
http://localhost:8080/configure
```

* Look for the `Jenkins URL` field and replace the value `10.0.0.5:8080` the URL of your Jenkins master `http://fqdn_of_your_jenkinsmaster/`

![Jenkins URL](<../assets/jenkins2/pre/13.jpg>)

* Click **Save** after changing the URL.


**7.** Install git

* Switch to the Putty SSH session that was opened during step 1.

* Type the following command:
```
sudo apt-get install git -y
```

### Task 3: Configure Jenkins  
The three tools that we will need to create our pipeline on Jenkins needs to be configured on our instance of Jenkins.


**1.** Configure the JDK

* Access the manage page from Jenkins master that you have configured.
```
http://localhost:8080/manage 
```

* Go to the **Global Tool Configuration**

![Global Tool Configuration](<../assets/jenkins2/pre/13.1.jpg>)

* In order to build the Parts Unlimited application we need to have the JDK installed. Click on **Add JDK**

> * Type the friendly name for the JDK: "JDK 8"
> * Un-check the box "Install automatically"
> * Set JAVA_HOME to `/usr/lib/jvm/java-8-openjdk-amd64`

![Add JDK](<../assets/jenkins2/pre/14.jpg>)

**2.** Configure Gradle 

Gradle will be used to build the Parts Unlimited application. If needed, you could use Maven or Ant as well, the configuration would be very similar.

* On to the **Global Tool Configuration**.
* Click on **Add Gradle** 
> * Type the friendly name fo this installation of Gradle: "Gradle"
> * Verify that the "Install automatically" box is checked
> * Select the latest version of Gradle in the drop-down list, current `Gradle 4.1`.

![Add Gradle](<../assets/jenkins2/pre/15.jpg>)

* Click **Save**, on the bottom of the page.

Next steps
----------

In this lab, you learned how to setup a Jenkins Master in Azure, try these labs for the next steps: 

- [Parts Unlimited MRP Continous Integration](2017-08-22-fund-11-Oth-CI.md)
- [Parts Unlimited MRP Continous Deployment](2017-08-22-fund-12-Oth-CD.md)

# Continuous Feedbacks

#### Issues / Questions about this HOL ??

[If you are encountering some issues or questions during this Hands on Labs, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)

Thanks
