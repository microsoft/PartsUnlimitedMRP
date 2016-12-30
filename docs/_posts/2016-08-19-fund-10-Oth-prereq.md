---
layout: page
title: Set up Parts Unlimited MRP with Jenkins
category: FundOth
order: 1
---

### Set up Parts Unlimited MRP with Jenkins ###

In this lab, we will set up the Jenkins Master that will be used for the PartsUnlimitedMRP project. [Jenkins](https://jenkins.io/) is an open source automation server that provides capabilities for building, deploying and automating any project. This lab will configure the pre-requisites on the Jenkins Master, if you are familiar with Jenkins and already have a Jenkins Master, you may want to skip to the next step for [Parts Unlimited MRP Continous Integration](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Integration).


### Pre-Requisites: ###

- An Active Azure Subscription (You can open a trial subscription here: [https://azure.microsoft.com/en-us/free](https://azure.microsoft.com/en-us/free))
- A tool to connect to a Linux vm using SSH (like Putty for example: [http://www.chiark.greenend.org.uk/~sgtatham/putty/](http://www.chiark.greenend.org.uk/~sgtatham/putty/)) 


### Tasks Overview:
The following tasks will install a virtual machine running Ubuntu with Jenkins in Azure then perform the basic configuration of Jenkins.

### Task 1: Create you Jenkins Master in Azure 
We will use the preconfigured VM image that is available on the Azure Market place to deploy our Jenkins master.

**1.** To deploy the Jenkins Master, simply click on the following button and fill in the fields.

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fdcaro%2FPartsUnlimitedMRP%2Fmaster%2Fdocs%2Fassets%2Fjenkins%2Fenv%2FJenkinsMaster.json" target="_blank">
        <img src="http://azuredeploy.net/deploybutton.png"/>
</a>

* Select the subscription where you want that virtual machine to be deployed
* Click **Create new** for the resource group
* Select the location closest to you
* Type the prefix for the FQDN of the Jenkins master
* Type the password for the jenkinsadmin user account 

![Deploy Jenkins Master image](<../assets/jenkins/jenkins_master_deploy.png>)

**2.** Click on the **Purchase** button.

Wait until the deployment complete. 


**3.** Obtain the public IP of the Jenkins master VM. 

On the Azure portal, click on **Resource Group** and look for the Resource Group that you have just created.

Click on the virtual machine in the resource group (_pumrp-jenkins_ in this example) and look for the "Public IP address/DNS name label".

![Obtain the public IP of the Jenkins Master](<../assets/jenkins/jenkinsmaster_ip.png>)

**4.** SSH to the Jenkins Master VM

Open PuTTY (or any other ssh tool that you like) and type the following information to logon to the VM:
```
jenkinsadmin@pumrp-jenkins.westus.cloudapp.azure.com
```
You will need to adjust the connection to what you have specified in the first step.
The user is jenkinsadmin in this lab. 

![SSH to the Jenkins Master VM](<../assets/jenkins/putty_to_jenkinsmaster.png>){:height="350px"}

Then type the password that you have specified.

### Task 2: Configure your Jenkins Master
In this task, we will perform the basic configuration of the Jenkins master server and install the necesary plugins that will be used for Continuous Integration.

**1.** Obtain the initial admin password

From the SSH session that you have opened at the end of Task 1, type the following command to obtain the initial admin password.

``` bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Copy the value returned by the command.
Keep the SSH session open, we will return at the end of this task.

![Initial jenkins admin password](<../assets/jenkins/initial_jenkins_password.png>) 

**2.** Unlock the jenkins master

With your browser, navigate to the default page of the Jenkins master. 

```
http://fqdn_of_your_jenkinsmaster:8080
```

Paste the initial admin password obtained earlier to unlock your instance of Jenkins and click **Continue**.

![Initial jenkins admin password](<../assets/jenkins/initial_jenkins_unlock.png>){:width="500px"}

Click **Install suggested plugins** 

![Initial jenkins plugins](<../assets/jenkins/initial_jenkins_plugins.png>){:width="500px"}

**3.** Create the first user 

Create a user from the "Create First Admin User": 
* Username = jenkins
* Password = Passw0rd
* Click **Save and Finish**. 

![Initial jenkins plugins](<../assets/jenkins/first_jenkins_user.png>){:width="500px"}

**4.** Restart Jenkins

Click the **Restart** button to restart Jenkins. 

![Initial jenkins plugins](<../assets/jenkins/restart_jenkins.png>){:width="500px"}

You now have a virtual machine in Azure running Jenkins as a Master.

**5.** Logon again on the jenkins master and navigate to the "Configure System" page:

```
http://fqdn_of_your_jenkinsmaster:8080/configure
```
Look for the Jenkins URL field and type the URL of your Jenkins master: http://fqdn_of_your_jenkinsmaster:8080/

![Jenkins URL](<../assets/jenkins/jenkins_url.png>)

Click **Save** 

**5.** Install git

Switch to the SSH session that was opened during step 1.

Type the following command:
```
sudo apt-get install git -y
```


Next steps
----------

In this lab, you learned how to setup a Jenkins Master in Azure, try these labs for the next steps: 

- [Parts Unlimited MRP Continous Integration](https://microsoft.github.io/PartsUnlimitedMRP/fundoth/fund-11-Oth-CI.html)
- [Parts Unlimited MRP Continous Deployment](https://microsoft.github.io/PartsUnlimitedMRP/fundoth/fund-12-Oth-CD.html)

# Continuous Feedbacks

#### Issues / Questions about this HOL ??

[If you are encountering some issues or questions during this Hands on Labs, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)

Thanks
