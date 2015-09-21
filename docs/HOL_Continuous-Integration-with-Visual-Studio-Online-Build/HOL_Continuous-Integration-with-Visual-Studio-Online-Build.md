HOL - Parts Unlimited MRP App Continuous Integration with Visual Studio Online Build
====================================================================================

In this lab we have an application called Parts Unlimited MRP. We want to set up
Visual Studio Online to be able continuously integrate code into the master
branch of code. This means that whenever code is committed and pushed to the
master branch, we want to ensure that it integrates into our code correctly to
get fast feedback. To do so, we are going to be setting up a build agent that
will allow us to compile and run unit tests on our code every time a commit is
pushed to Visual Studio Online.

###Pre-requisites:###

-   An active Visual Studio Online account

-   An active Azure subscription

-   Project Admin rights to the Visual Studio Online account

### Tasks Overview: ###

**Set up your Visual Studio Online account:** This step helps you download the source code, and then push it to your own Visual Studio Online account.

**Set up Linux virtual machine in Azure as the Build Agent:** In this step, you will create a new Linux machine, install all the dependencies required to be a build machine, and then configure a build agent on it.

**Create Continuous Integration Build:** In this step, you will create a build definition in Visual Studio Online that will be triggered every time a commit is pushed to your repository in Visual Studio Online. 

### Pre-Requisite: Set up your Visual Studio Online account

We want to push the application code to your Visual Studio Online account in
order to use Build.



**1.** First, we need to enable secondary credentials. Go to your **account home
page**:

	https://<account>.visualstudio.com

**2. Click on your name** in the top right, and then click **My profile**

![](<media/my_profile.png>)

**3.** This takes you to your profile page. Complete the following actions:

-   Click **Security**

-   Click **Alternate authentication credentials**

-   Click the check box labeled **Enable alternate authentication credentials**

-   Enter **secondary user name** and a **password**

-   Click **Save**

![](<media/alternate_credentials.png>)

These credentials will be used when interacting with the git repository from the
command line.

**4.** Clone the **PartsUnlimitedMRP** git repository located in GitHub:

    git clone https://github.com/Microsoft/PartsUnlimitedMRP.git

![](<media/clone_mrp.png>)

**NOTE:** you must install Git before you can run Git clone from the command line.

    sudo apt-get install git

**5.** Add your Visual Studio Online repository as a new remote called **vso** and push to it
your Visual Studio Online account. While pushing, use the user name (secondary) and password you have created when enabling alternate authentication credentials earlier in the lab.

	cd PartUnlimitedMRP/
	git remote add vso <url_to_repository>
	git push -u vso --all
	
![](<media/push_to_vso.png>)

**NOTE:** we added the Visual Studio Online repository as a remote named **vso**, so we need to
push to that remote in the future for our changes to appear in our Visual Studio Online
repository.

**6.** Your Visual Studio Online account should not have a copy of the PartsUnlimitedMRP
application:

![](<media/mrp_in_vso.png>)

 

### 1. Set up Linux virtual machine in Azure as the Build Agent

The application is written in Java, so we are going to use a Linux machine to
build it.

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

	# Install Gradle, Java, and MongoDB
	sudo apt-get install gradle -y
	sudo apt-get install openjdk-8-jdk openjdk-8-jre mongodb -y

	# Set environment variables for Java
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
	export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin
	
	# Install node and npm
	curl --silent --location https://deb.nodesource.com/setup_0.12 | sudo bash -
	sudo apt-get install -y nodejs

**9.** Our build server is ready to install a build agent on it, but first we
need to create a new build agent pool. Go to your **account home page**:

	https://<account>.visualstudio.com

**10.** Click the **gear in the top right** of the page to go the administration
page

**11.** Go to the **Agent pools** tab and then click **New pool...**

![](<media/new_agent_pool.png>)

**12.** Enter **linux** for the name of the agent pool, and then click **OK**

![](<media/agent_pool_details.png>)

**13.** We are not ready to install the agent installer once globally on our
build machine. This doesn't install an agent, it simply pulls down the agent
installer. Go back to the ssh session, and **enter these commands** to install
the Visual Studio Online agent installer:

**NOTE:** Do not change $USER with your user, keep it as $USER.

	sudo npm install vsoagent-installer -g
	sudo chown -R $USER ~/.npm

**14.** Create an agent by running the following commands:

	cd ~/
	mkdir myagent
    cd myagent
	vsoagent-installer

This installs the agent to the directory **~/myagent**.

**15.** The first time we run the agent, it will be configured. Authorize Agent at Visual Studio Online. 

**15.1** Go to your Visual Studio Tenant and to settings:

Go to the **Agent pools** tab and then...

![](<media/new_agent_pool.png>)

...put your agent it to a group **Agent Pool Service Accounts**

![](<media/vso_agent_pool.png>)

**15.2** Run the agent with the following command:

	node agent/vsoagent

**16.** Enter the following information when prompted:

-   Alternate username

-   Alternate password

-   Server URL (Visual Studio Online URL)

-   Agent name (press enter for default)

-   Agent pool (enter in **linux** - the pool created earlier in this lab)

![](<media/start_agent.png>)

And now, you have a build agent configured for Visual Studio Online.

 

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

**6.** Select the first Gradle task and **edit the task name** to say
*IntegrationService* and set the **Gradle Wrapper** to the following location:

	src/Backend/IntegrationService/gradlew 

Set the **Working Directory** to the following location:

	src/Backend/IntegrationService

![](<media/build_gradle_integration.png>)

**7.** Select the second Gradle task and **edit the task name** to say
*OrderService* and set the **Gradle Wrapper** to the following location:

	src/Backend/OrderService/gradlew

Set the **Working Directory** to the following location:

	src/Backend/OrderService

![](<media/build_gradle_order.png>)

**8.** Select the third Gradle task and **edit the task name** to say *Clients*
and set the **Gradle Wrapper** to the following location:

	src/Clients/gradlew

Set the **Working Directory** to the following location:

	src/Clients

![](<media/build_gradle_clients.png>)

**9.** Click **Add build step...** and add a **Publish Build Artifacts** task


![](<media/build_add_pub_step.png>)

**10.** Select the Publish Build Artifacts task, and fill in the input values
with the following:

	Copy Root: $(build.sourcedirectory)
	Contents: **/build/libs/!(buildSrc)*.?ar
	Artifact Name: drop
	Artifact Type: Server

![](<media/build_pub_step_details.png>)

**NOTE:** The Content field supports minimatch patterns. You can find more information here: [https://github.com/Microsoft/vso-agent-tasks](https://github.com/Microsoft/vso-agent-tasks "https://github.com/Microsoft/vso-agent-tasks")

**11.** Go to the **Triggers** tab and **select Continuous Integration (CI)**

![](<media/build_ci_trigger.png>)

**12.** Click **Save**, give the build definition a name (i.e.
*PartsUnlimitedMRP.CI*), and then click **Ok**

![](<media/build_save.png>)

**13.** Go to the **Code** tab, select the **index.html** file located at
src/Clients/Web, and click **Edit**

![](<media/edit_index_web.png>)

**14.** Change the **Parts Unlimited MRP** and then
click the **save button**.
![](<media/save_index.png>)

**15.** This should have triggered the build definition we previously created,
and you should get a build summary similar to this, which includes test results:

![](<media/build_summary.png>)

 

Next steps
----------

In this lab, you learned how to push new code to Visual Studio Online, create a Virtual Machine
in Azure, install a Visual Studio Online Build Agent to an Ubuntu server, and create a Continuous
Integration build that runs when new commits are pushed to the master branch.
This allows you to get feedback as to whether your changes made breaking syntax
changes, or if they broke one or more automated tests, or if your changes are a
okay. Try these labs out for next steps:

-   HOL Parts Unlimited MRP Continuous Delivery

-   HOL Parts Unlimited MRP Automated Testing

-   HOL Parts Unlimited MRP Application Performance Monitoring
