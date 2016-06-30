HOL - Parts Unlimited MRP App Continuous Integration with Visual Studio Team Services Build
====================================================================================

In this lab we have an application called Parts Unlimited MRP. We want to set up
Visual Studio Team Services to be able continuously integrate code into the master
branch of code. This means that whenever code is committed and pushed to the
master branch, we want to ensure that it integrates into our code correctly to
get fast feedback. To do so, we are going to be setting up a build agent that
will allow us to compile and run unit tests on our code every time a commit is
pushed to Visual Studio Team Services.

###Pre-requisites:###

-   An active Visual Studio Team Services (VSTS) account

-   An active Azure subscription

-   Project Admin rights to the Visual Studio Team Services account

### Tasks Overview: ###

**Set up your Visual Studio Team Services account:** This step helps you download the source code, and then push it to your own Visual Studio Team Services account.

**Set up Linux virtual machine in Azure as the Build Agent:** In this step, you will create a new Linux machine, install all the dependencies required to be a build machine, and then configure a build agent on it.

**Create Continuous Integration Build:** In this step, you will create a build definition in Visual Studio Team Services that will be triggered every time a commit is pushed to your repository in Visual Studio Team Services. 

### Pre-Requisite: Set up your Visual Studio Team Services account

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

**3.** Add your Visual Studio Team Services repository as a new remote called **vso** and push to it
your Visual Studio Team Services account. While pushing, use the user name (secondary) and password you have created when enabling alternate authentication credentials earlier in the lab.

	cd PartsUnlimitedMRP/
	git remote add vsts <url_to_repository>
	git push -u vsts --all
	
![](<media/push_to_vsts.png>)

**NOTE:** we added the Visual Studio Team Services repository as a remote named **vsts**, so we need to
push to that remote in the future for our changes to appear in our Visual Studio Team Services
repository.

**4.** Your Visual Studio Team Services account should now have a copy of the PartsUnlimitedMRP
application:

![](<media/mrp_in_vsts.png>)

 

### 1. Set up Linux virtual machine in Azure as the Build Agent

The application is written in Java, so we are going to use a Linux machine to
build it.

HINT: You can use OSX, Ubuntu, or a RedHat VM to run the agent. This guide is based on an Ubuntu VM.

**1.** Go to <https://portal.azure.com>

**2.** Click the **New** button in the top left corner of the Azure portal

![](<media/azure_new_resource.png>)

**3.** Select **Compute**, then **Ubuntu Server 14.04 LTS**, and click
**Create**

![](<media/new_ubuntu_vm.png>)

**4.** Enter a **host name**, **user name**, and **password**. Click **Create**

![](<media/create_vm_opts.png>)

**5.** Once the machine has been created select the **tile for the machine**,
click on **Endpoints**, and note the public **DNS name** and the **public port**
that was chosen for SSH access.

![](<media/ssh_details.png>)

**6.** Connect via SSH to the new Linux machine using the **public DNS name** and **public port** from step five.

	ssh <user>@<public_dns> -p <public_port>

![](<media/create_ssh.png>)

**7.** If you are on **Ubuntu 14.04**, run these commands; otherwise, **ignore
this step**:

Press [ENTER] to continue when asked after the first command.

	sudo add-apt-repository ppa:openjdk-r/ppa
	sudo apt-get update

**8.** Copy and paste the following snippet to **run these commands**:

	# Install git client
	sudo apt-get install git -y

	# Install libunwind, libcurl, and libicu
	sudo apt-get install -y libunwind8 libcurl3 libicu52 

	# Install Gradle, Java, and MongoDB
	sudo apt-get install gradle -y
	sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb -y

	# Set environment variables for Java
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
	export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
	
	
**9.** Our build server is ready to install a build agent on it, but first we
need to create a new build agent pool. Go to your **account home page (not the PartsUnlimitedMRP code page)**:

	https://<account>.visualstudio.com

**10.** Click the **gear in the top right** of the page to go the administration
page

**11.** Go to the **Agent pools** tab and then click **New pool...**

![](<media/new_agent_pool.png>)

**12.** Enter **linux** for the name of the agent pool, and then click **OK**

![](<media/agent_pool_details.png>)

**13.** Go to the newly created pool “**linux**”, Choose the Roles link and select the pool to see the details. Choose Add, and search for the user or group you want to add. You can view the contact card for users and groups.
![](<media/vsts_agent_pool.png>)

**NOTE:** By default, the two built-in security groups Agent Pool Administrators and Agent Pool Service Accounts are associated with each agent pool. Members of the first group have permission to register new agents into the pool. Members of the second group have permission to list to queued jobs in the agent pool. You can view, add, and remove security groups and users independently for each agent pool. However, you cannot add groups that are part of a project collection, project, or team.

We are now ready to configure the Visual Studio Team Services Build and Release Agent. This guide is based on Ubuntu but you may run the agent in Ubuntu, OSX, and RedHat. For information you may visit this [link](https://github.com/Microsoft/vsts-agent/blob/master/README.md).

**14.** First we will download the agent and extract it:

	mkdir myagent && cd myagent
	wget https://github.com/Microsoft/vsts-agent/releases/download/v2.102.1/vsts-agent-ubuntu.14.04-x64-2.102.1.tar.gz
	tar xzf ~/Downloads/vsts-agent-ubuntu.14.04-x64-2.102.1.tar.gz

**15.** The first time we run the agent, it will be configured.
```bash
./config.sh
```
**NOTE:** You need to use your own VSTS account (https://<account\>.visualstudio.com) and the Personal Access Token (PAT).

```bash
>> Connect:

Enter server URL > https://<account>.visualstudio.com
Enter authentication type (press enter for PAT) >
Enter personal access token > ****************************************************
Connecting to server ...
Saving credentials...

>> Register Agent:

Enter agent pool (press enter for default) > linux 
Enter agent name (press enter for mymachine) > myAgentName
Scanning for tool capabilities.
Connecting to the server.
Successfully added the agent
Enter work folder (press enter for _work) >
2016-05-27 11:03:33Z: Settings Saved.
Enter run agent as service? (Y/N) (press enter for N) >
```

**16.**  Run the agent with the following command:

	./run.sh

And now, you have a build agent configured for Visual Studio Team Services.

 

### 2. Create Continuous Integration Build

A continuous integration build will give us the ability check whether the code
we checked in can compile and will successfully pass any automated tests that we
have created against it.

**1.** Go to your **account’s homepage**: https://<account\>.visualstudio.com

**2.** Click **Browse** and then select your team project and click
**Navigate**.

![](<media/navigate_to_project.png>)

**3.** Once on the project’s home page, click on the **Build** hub at the top of
the page.

![](<media/build_tab.png>)

**4.** Click the **green “plus” sign**, select **Empty**, and then click **OK**.

![](<media/new_empty_build.png>)

**5.** Click on the **Repository** tab, and choose the git repository that
PartsUnlimitedMRP source is in.

![](<media/build_select_repo.png>)

**6.** Click on the **Build** tab, click **Add build step...**, and then **add
three Gradle** tasks to the script

![](<media/build_add_gradle.png>)

**7.** Select the first Gradle task and **edit the task name** to say
*IntegrationService* and set the **Gradle Wrapper** to the following location:

	src/Backend/IntegrationService/gradlew 

Set the **Working Directory** to the following location:

	src/Backend/IntegrationService

![](<media/build_gradle_integration.png>)

**8.** Select the second Gradle task and **edit the task name** to say
*OrderService* and set the **Gradle Wrapper** to the following location:
(NOTE: set the Options to **-x text** as the test have external dependencies on a mongo database.)

	src/Backend/OrderService/gradlew
	-x test
	

Set the **Working Directory** to the following location:

	src/Backend/OrderService

![](<media/build_gradle_order.png>)

**9.** Select the third Gradle task and **edit the task name** to say *Clients*
and set the **Gradle Wrapper** to the following location:

	src/Clients/gradlew

Set the **Working Directory** to the following location:

	src/Clients

![](<media/build_gradle_clients.png>)

**10.** Click **Add build step...** and add a **Publish Build Artifacts** task


![](<media/build_add_pub_step.png>)

**11.** Select the Publish Build Artifacts task, and fill in the input values
with the following:

	Copy Root: $(build.sourcedirectory)
	Contents: **/build/libs/!(buildSrc)*.?ar
	Artifact Name: drop
	Artifact Type: Server

![](<media/build_pub_step_details.png>)

**NOTE:** The Content field supports minimatch patterns. You can find more information here: [https://github.com/Microsoft/vso-agent-tasks](https://github.com/Microsoft/vso-agent-tasks "https://github.com/Microsoft/vso-agent-tasks")

**12.** Go to the **Triggers** tab and **select Continuous Integration (CI)**

![](<media/build_ci_trigger.png>)

**13.** Click **General**, set the default queue to the previously created queue (**linux**)

![](<media/build_general.png>)

**14.** Click **Save**, give the build definition a name (i.e.
*PartsUnlimitedMRP.CI*), and then click **Ok**

![](<media/build_save.png>)

**15.** Go to the **Code** tab, select the **index.html** file located at
src/Clients/Web, and click **Edit**

![](<media/edit_index_web.png>)

**16.** Change the **Parts Unlimited MRP** and then
click the **save button**.
![](<media/save_index.png>)

**17.** This should have triggered the build definition we previously created,
and you should get a build summary similar to this, which includes test results:

![](<media/build_summary.png>)

 

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
