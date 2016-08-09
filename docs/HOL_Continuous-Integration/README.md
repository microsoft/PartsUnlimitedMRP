HOL - Parts Unlimited MRP App Continuous Integration with Visual Studio Team Services Build
====================================================================================

In this lab we have an application called Parts Unlimited MRP. We want to set up
Visual Studio Team Services to be able continuously integrate code into the master
branch of code. This means that whenever code is committed and pushed to the
master branch, we want to ensure that it integrates into our code correctly to
get fast feedback. To do so, we are going to be setting up a build agent that
will allow us to compile and run unit tests on our code every time a commit is
pushed to Visual Studio Team Services.

## Video ##

Click on this image to see the Channel 9 video with the step by step about this HOL :

[![](http://i.giphy.com/hCpRR7re6BR60.gif)](https://channel9.msdn.com/Blogs/TalkDevOps/Parts-Unlimited-MRP-HOL-Continuous-Integration)

## Pre-requisites: ##

-   An active Visual Studio Team Services (VSTS) account

-   An active Azure subscription

-   Project Admin rights to the Visual Studio Team Services account

## Tasks Overview: ##

**Set up your Visual Studio Team Services account:** This step helps you download the source code, and then push it to your own Visual Studio Team Services account.

**Set up Linux virtual machine in Azure to be used as the Build Agent:** In this step, you will create a new Linux machine, install all the dependencies required to be a build machine, and then configure a build agent on it.

**Create Continuous Integration Build:** In this step, you will create a build definition in Visual Studio Team Services that will be triggered every time a commit is pushed to your repository in Visual Studio Team Services. 

## Pre-Requisite: Set up your Visual Studio Team Services account ##

We want to push the application code to your Visual Studio Team Services account in
order to use Build.

**1.** First, we need to authenticate access to Visual Studio Team Services secondary credentials. Follow the steps in this [link](https://www.visualstudio.com/en-us/docs/setup-admin/team-services/use-personal-access-tokens-to-authenticate) 
 to create a personal access token (PAT).

**NOTE:** These Personal Access Token (PAT) will be used when interacting with the git repository from the
command line. Make sure you copy the token now. Visual Studio Team Services does not store it and you won't be able to see it again. 


**2.** Clone the **PartsUnlimitedMRP** git repository located in GitHub:

    git clone https://github.com/Microsoft/PartsUnlimitedMRP.git

![](<media/clone_mrp.png>)

**NOTE:** If you are running Linux on your local workstation install git:

    sudo apt-get install git
	
**NOTE:** If you are running Windows, you can install the git client from here:

	http://git-scm.com/download

**NOTE:** If you don't have created a project in your VSTS subscription yet, just click on the **New** button in the Home page

![](<media/vsts_create_project.png>)

**3.** Add your Visual Studio Team Services repository as a new remote called **vsts** and push to it
your Visual Studio Team Services account. While pushing, use the user name (secondary) and password you have created when enabling alternate authentication credentials earlier in the lab.

```bash
cd PartsUnlimitedMRP/

git remote add vsts <url_to_repository>

git push -u vsts --all
```	
![](<media/push_to_vsts.png>)

**NOTE:** if you want to know the URL of your repository in your VSTS subscription, you can have this information from the **CODE** tab :

![](<media/vsts_infos.png>)

**NOTE:** we added the Visual Studio Team Services repository as a remote named **vsts**, so we need to
push to that remote in the future for our changes to appear in our Visual Studio Team Services
repository.

**4.** Your Visual Studio Team Services account should now have a copy of the PartsUnlimitedMRP
application (you may need to refresh you page to see the result) :

![](<media/mrp_in_vsts.png>)

## 1. Set up Linux virtual machine in Azure as the Build Agent ##

The application is written in Java, so we are going to use a Linux machine to
build it.

HINT: You can use OSX, Ubuntu, or a RedHat VM to run the agent. This guide is based on an Ubuntu VM.

**1.** Go to <https://portal.azure.com>

**2.** Click the **New** button in the top left corner of the Azure portal

![](<media/azure_new_resource.png>)

**3.** Select **Compute**, search for **Ubuntu**, click on **Ubuntu Server 16.04 LTS** and then
**Create**

![](<media/new_ubuntu_vm.png>)

**4.a.** Fill the basics informations such as **host name**, **user name**, and **password**. Click **Create**

![](<media/create_vm_opts.png>)

**4.b.** For the others options, you can keep the default one. For the size, one machine with around 1 CPU and 2 Go of RAM is enough.

![](<media/create_vm_opts2.png>)

**5.** Once the machine has been created select the **Public IP of the machine**,
click on **Configuration**, and add a  **DNS name label**. Finnaly save the Configuration.

![](<media/ssh_details.png>)

**6.** Connect via SSH to the new Linux machine using the **username** previously selected on step 4.a and the **DNS name label** from step five.

	ssh <user>@<public_dns>

![](<media/create_ssh.png>)

**7.** First step is to update the repository list and upgrade the current packages installed thanks to :
```bash
# Update repository list and Upgrade the current packages already installed
sudo apt-get update && sudo apt-get upgrade
```
**8.** Copy and paste the following snippet to **run these commands**:
```bash
# Prerequisites for VSTS Agent
sudo apt-get install -y libunwind8 libcurl3 libcurl4-openssl-dev

# Install Gradle, Java, and MongoDB
sudo apt-get install gradle -y
sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb -y

# Set environment variables for Java
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
```	
**9.** Our build server is ready to install a build agent on it, but first we
need to create a new build agent pool. Go to your **account home page (not the PartsUnlimitedMRP code page)**:

	https://<account>.visualstudio.com

**10.** Click the **gear in the top right** of the page to go the administration
page

**11.** Go to the **Agent pools** tab and then click **New pool...**

![](<media/new_agent_pool.png>)

**12.** Enter **Linux-PartsUnlimitedMRP** for the name of the agent pool, and then click **OK**

![](<media/agent_pool_details.png>)

We are now ready to configure the Visual Studio Team Services Build and Release Agent. This guide is based on Ubuntu but you may run the agent in Ubuntu, OSX, and RedHat. For information you may visit this [link](https://github.com/Microsoft/vsts-agent/blob/master/README.md).

**14.** First we will download the agent and extract it:
```bash
mkdir myagent && cd myagent

wget https://github.com/Microsoft/vsts-agent/releases/download/v2.104.1/vsts-agent-ubuntu.16.04-x64-2.104.1.tar.gz

tar xzf vsts-agent-ubuntu.16.04-x64-2.104.1.tar.gz
```
**15.** The first time we run the agent, it will be configured.
```bash
./config.sh
```
**NOTE:** You need to use your own VSTS account (https://<account\>.visualstudio.com) and the Personal Access Token (PAT). [Check this article to setup one PAT in VSTS](https://gist.github.com/julienstroheker/f452b27927337e97cf2ac9f7262cdadc)

Example :
```bash
Julien@PartsUnlimitedMRPAgent:~/myagent$ ./config.sh

>> End User License Agreements:

Building sources from a TFVC repository requires accepting the Team Explorer Everywhere End User License Agreement. 
This step is not required for building sources from Git repositories.

A copy of the Team Explorer Everywhere license agreement can be found at:
  /home/Julien/myagent/externals/tee/license.html

Enter accept the Team Explorer Everywhere license agreement now? (Y/N) (press enter for N) > Y

>> Connect:

Enter server URL > https://julienstroheker.visualstudio.com/
Enter authentication type (press enter for PAT) >
Enter personal access token > ****************************************************
Connecting to server ...

>> Register Agent:

Enter agent pool (press enter for default) > Linux-PartsUnlimitedMRP
Enter agent name (press enter for PartsUnlimitedMRPAgent) > Linux-PartsUnlimitedMRP-Agent01
Scanning for tool capabilities.
Connecting to the server.
Successfully added the agent
Testing agent connection.
Enter work folder (press enter for _work) >
2016-08-05 19:49:24Z: Settings Saved.
```

**16.**  Run the agent with the following command:

	./run.sh

And now, you have a build agent configured for Visual Studio Team Services.

![](<media/run_agent.png>)

## 2. Create Continuous Integration Build ##

A continuous integration build will give us the ability check whether the code we checked in can compile and will successfully pass any automated tests that we have created against it.

**1.** Go to your **account’s homepage**: https://<account\>.visualstudio.com

**2.** Click **Browse** and then select your team project and click
**Navigate**.

![](<media/navigate_to_project.png>)

**3.** Once on the project’s home page, click on the **Build** hub at the top of
the page.

![](<media/build_tab.png>)

**4.** Click the **green “plus” sign**, select an **Empty** template, and then click **Next >**.

![](<media/new_empty_build.png>)

**5.** Make sure, the repository and the branch are with the default option. Change the **Default agent queue** for the one previously created (Linux-PartsUnlimitedMRP) and then click on **Create**

![](<media/build_select_repo.png>)

**6.** Click on **+ Add build step...** and search for the **Gradle** task, then click three time on **Add**

![](<media/build_add_gradle.png>)

**7.** Next, search for **Copy Publish Artifact** task and click on **Add** and **Close**

![](<media/build_add_pub_step.png>)

**8.** Select the first Gradle task and **edit the task name** to say
*IntegrationService* and set the **Gradle Wrapper** to the following location:

	src/Backend/IntegrationService/gradlew 

Set the **Working Directory** to the following location:

	src/Backend/IntegrationService

![](<media/build_gradle_integration.png>)

**9.** Select the second Gradle task and **edit the task name** to say
*OrderService* and set the **Gradle Wrapper** to the following location:

	src/Backend/OrderService/gradlew	

Set the **Working Directory** to the following location:

	src/Backend/OrderService

![](<media/build_gradle_order.png>)

**10.** Select the third Gradle task and **edit the task name** to say *Clients*
and set the **Gradle Wrapper** to the following location:

	src/Clients/gradlew

Set the **Working Directory** to the following location:

	src/Clients

![](<media/build_gradle_clients.png>)

**11.** Select the **Copy and Publish Artifact** task, and fill in the input values with the following:

	Copy Root: 
	Contents: **/build/libs/!(buildSrc)*.?ar
	Artifact Name: drop
	Artifact Type: Server

![](<media/build_pub_step_details.png>)

**NOTE:** The Content field supports minimatch patterns. You can find more information here: [https://github.com/Microsoft/vso-agent-tasks](https://github.com/Microsoft/vso-agent-tasks "https://github.com/Microsoft/vso-agent-tasks")

**12.** Go to the **Triggers** tab and **select Continuous Integration (CI)**

![](<media/build_ci_trigger.png>)

**13.** Click **General**, check if the default queue is correctly setted to the previously created queue (**Linux-PartsUnlimitedMRP**)

![](<media/build_general.png>)

**14.** Click **Save**, give the build definition a name (i.e.
*PartsUnlimitedMRP.CI*), and then click **Ok**

![](<media/build_save.png>)

**NOTE:** We just configured our **Build** and activated the **Continuous Integration** trigger. Now let's test if everything works well !

**15.** Go to the **Code** tab, select the **index.html** file located at
src/Clients/Web, and click **Edit**

![](<media/edit_index_web.png>)

**16.** Change the title for **Parts Unlimited MRP** and then click the **Commit button**.

![](<media/save_index.png>)

**NOTE:** When you click on commit, you should see a blue notification appear on the top of your page, with the ID of your commit

![](<media/build_commited.png>)

**17.** This should have triggered the build definition we previously created, and you should get a build summary similar to this, which includes test results:

![](<media/build_summary.png>)

**NOTE:** To have access at all your build history, you have to go on **Build** section, and from there you should be able to see your history **Completed** or **Queued**, you just have to double click on it ahve details.

![](<media/build_get_summary.png>)
 
Next steps
----------

In this lab, you learned how to push new code to Visual Studio Team Services, create a Virtual Machine
in Azure, install a Visual Studio Team Services Build Agent to an Ubuntu server, and create a Continuous
Integration build that runs when new commits are pushed to the master branch.
This allows you to get feedback as to whether your changes made breaking syntax
changes, or if they broke one or more automated tests, or if your changes are a
okay. Try these labs out for next steps:

-   [Parts Unlimited MRP Continuous Deployment](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment)

-   [Parts Unlimited MRP Automated Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Automated-Testing)

-   [Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)

-	[Parts Unlimited MRP Auto-Scaling and Load Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Autoscaling-Load-Testing)
